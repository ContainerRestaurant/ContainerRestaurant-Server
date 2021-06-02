package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.feed.container.ContainerService;
import container.restaurant.server.domain.feed.hit.FeedHit;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.push.event.FeedHitEvent;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.level.UserLevelFeedCountService;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final StatisticsService statisticsService;
    private final ContainerService containerService;
    private final UserLevelFeedCountService userLevelFeedCountService;
    private final ImageService imageService;

    private final FeedLikeRepository feedLikeRepository;
    private final ScrapFeedRepository scrapFeedRepository;
    private final FeedHitRepository feedHitRepository;

    private final PagedResourcesAssembler<Feed> feedAssembler;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public FeedDetailDto getFeedDetail(Long feedId, Long loginId) {
        if (loginId != null && !checkHit(loginId, feedId))
            updateHit(loginId, feedId);

        Feed feed = findById(feedId);
        return FeedDetailDto.builder()
                .feed(feed)
                .thumbnailUrl(ImageService.getUrlFromImage(feed.getThumbnail()))
                .isLike(feedLikeRepository.existsByUserIdAndFeedId(loginId, feedId))
                .isScraped(scrapFeedRepository.existsByUserIdAndFeedId(loginId, feedId))
                .build();
    }

    @Transactional(readOnly = true)
    public Feed findById(Long id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 피드입니다..(id:" + id + ")"));
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAll(Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByCategory(pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAll(pageable)),
                FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUser(
            Long userId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByOwnerIdAndCategory(
                                userId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByOwnerId(userId, pageable)),
                FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByRestaurant(
            Long restaurantId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByRestaurantIdAndCategory(
                                restaurantId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByRestaurantId(restaurantId, pageable)),
                FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUserScrap(
            Long userId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByScraperIdAndCategory(
                                userId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByScraperId(userId, pageable)),
                FeedPreviewDto::from);
    }

    @Transactional
    public void delete(Long feedId, Long userId) {
        Feed feed = findById(feedId);
        if (!feed.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("해당 피드를 삭제할 수 없습니다.");

        statisticsService.removeRecentUser(feed.getOwner());
        feed.getOwner().feedCountDown();
        feed.getRestaurant().feedCountDown();
        feed.getRestaurant().subDifficultySum(feed.getDifficulty());

        containerService.deleteAll(feed.getContainerList());
        feedHitRepository.deleteAllByFeed(feed);
        userLevelFeedCountService.levelFeedDown(feed);
        feedRepository.delete(feed);
    }

    @Transactional
    public Long createFeed(FeedInfoDto dto, Long ownerId) {
        User user = userService.findById(ownerId);
        Restaurant restaurant = restaurantService.findByDto(dto.getRestaurantCreateDto());
        Image thumbnail = imageService.findById(dto.getThumbnailImageId());

        statisticsService.addRecentUser(user);
        user.feedCountUp();
        restaurant.feedCountUp();
        restaurant.addDifficultySum(dto.getDifficulty());

        Feed feed = feedRepository.save(dto.toFeedWith(user, restaurant, thumbnail));
        containerService.save(dto.toContainerListWith(feed, restaurant));
        userLevelFeedCountService.levelFeedUp(feed);
        return feed.getId();
    }

    @Transactional
    public void updateFeed(Long feedId, FeedInfoDto dto, Long userId) {
        Feed feed = findById(feedId);
        if (!feed.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("해당 피드를 업데이트할 수 없습니다.");

        dto.updateSimpleAttrs(feed);
        updateRelationalAttrs(feed, dto);
    }

    private void updateRelationalAttrs(Feed feed, FeedInfoDto dto) {
        if (feed.getThumbnail() != null && !feed.getThumbnail().getId().equals(dto.getThumbnailImageId()))
            feed.setThumbnail(imageService.findById(dto.getThumbnailImageId()));

        // 업데이트할 리스트와 삭제할 리스트
        List<Container> newMenus = dto.toContainerListWith(feed, feed.getRestaurant());
        List<Container> toDelete = feed.getContainerList();

        Restaurant restaurant = restaurantService.findById(dto.getRestaurantCreateDto().getId());
        if (!feed.getRestaurant().getId().equals(restaurant.getId())) {
            // 식당이 바뀐 경우 식당을 업데이트하고, 기존 모든 메뉴 삭제
            feed.setRestaurant(restaurant);
        } else {
            // 식당이 그대로인 경우 동일한 이름의 메뉴를 삭제하지 않음
            Set<String> menuNames = newMenus.stream()
                    .map(c -> c.getMenu().getName())
                    .collect(Collectors.toSet());
            toDelete.removeIf(c -> menuNames.contains(c.getMenu().getName()));
        }
        containerService.deleteAll(toDelete);

        feed.setContainerList(newMenus);
        containerService.save(feed.getContainerList());
    }

    @Transactional
    public void updateHit(Long userId, Long feedId) {
        Feed feed = this.findById(feedId);
        feed.hit();
        feedHitRepository.save(
                FeedHit.of(userService.findById(userId), feed));
        publisher.publishEvent(new FeedHitEvent(feed));
    }

    @Transactional
    public boolean checkHit(Long loginId, Long feedId) {
        return feedHitRepository.existsByUserIdAndFeedId(loginId, feedId);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findForUpdatingRecommend(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, pageable);
    }
}