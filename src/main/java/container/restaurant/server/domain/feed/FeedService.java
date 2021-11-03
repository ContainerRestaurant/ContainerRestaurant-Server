package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.feed.hit.FeedHit;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.feed.recommend.RecommendFeedService;
import container.restaurant.server.domain.push.event.FeedHitEvent;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.level.UserLevelFeedCountService;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final StatisticsService statisticsService;
    private final RecommendFeedService recommendFeedService;
    private final UserLevelFeedCountService userLevelFeedCountService;
    private final ImageService imageService;

    private final FeedLikeRepository feedLikeRepository;
    private final ScrapFeedRepository scrapFeedRepository;
    private final FeedHitRepository feedHitRepository;
    private final CommentRepository commentRepository;

    private final PagedResourcesAssembler<Feed> feedAssembler;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public FeedDetailDto getFeedDetail(Long feedId, Long loginId) {
        if (loginId != null && !checkHit(loginId, feedId))
            updateHit(loginId, feedId);

        return FeedDetailDto.builder()
                .feed(findById(feedId))
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
    public PagedModel<FeedPreviewDto> findAll(Pageable pageable, Category categoryFilter, Long loginId) {
        Page<Feed> page = (categoryFilter == null || categoryFilter.toString().equalsIgnoreCase("ALL")) ?
                feedRepository.findAll(pageable) :
                feedRepository.findAllByCategory(pageable, categoryFilter);

        return feedAssembler.toModel(page, new FeedPreviewDtoAssembler(loginId, page));
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUser(
            Long userId, Long loginId, Pageable pageable, Category categoryFilter
    ) {
        Page<Feed> page = categoryFilter != null ?
                feedRepository.findAllByOwnerIdAndCategory(userId, pageable, categoryFilter) :
                feedRepository.findAllByOwnerId(userId, pageable);

        return feedAssembler.toModel(page, new FeedPreviewDtoAssembler(loginId, page));
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByRestaurant(
            Long restaurantId, Long loginId, Pageable pageable, Category categoryFilter
    ) {
        Page<Feed> page = categoryFilter != null ?
                feedRepository.findAllByRestaurantIdAndCategory(restaurantId, pageable, categoryFilter) :
                feedRepository.findAllByRestaurantId(restaurantId, pageable);

        return feedAssembler.toModel(page, new FeedPreviewDtoAssembler(loginId, page));
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUserScrap(
            Long userId, Long loginId, Pageable pageable, Category categoryFilter
    ) {
        Page<Feed> page = categoryFilter != null ?
                feedRepository.findAllByScraperIdAndCategory(userId, pageable, categoryFilter) :
                feedRepository.findAllByScraperId(userId, pageable);

        return feedAssembler.toModel(page, new FeedPreviewDtoAssembler(loginId, page));
    }

    @Transactional
    public void delete(Long feedId, Long userId) {
        Feed feed = findById(feedId);
        if (!feed.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("해당 피드를 삭제할 수 없습니다.");

        statisticsService.removeRecentUser(feed.getOwner());

        feed.getOwner().feedCountDown();
        feed.getRestaurant().feedCountDown(feed);
        feedHitRepository.deleteAllByFeed(feed);
        userLevelFeedCountService.levelFeedDown(feed);

        // 피드 -< 댓글, 스크랩, 좋아요 삭제 추가 작업 (container는 cascade 삭제되는 것으로 파악)
        commentRepository.deleteAllByFeed(feed);

        List<Long> scrapUserId= scrapFeedRepository.findUserIdByFeedId(feedId);
        if(!scrapUserId.isEmpty()){ // 피드 연관된 사용자 scrap count down
            for(Long scrapUser:scrapUserId){
                userService.findById(scrapUser).scrapCountDown();
            }
            scrapFeedRepository.deleteAllByFeed(feed);
        }

        feedLikeRepository.deleteAllByFeed(feed);

        feedRepository.delete(feed);

        recommendFeedService.checkAndDelete(feed);
    }

    @Transactional
    public Long createFeed(FeedInfoDto dto, Long ownerId) {
        User user = userService.findById(ownerId);
        Restaurant restaurant = restaurantService.findByDto(dto.getRestaurantCreateDto());
        Image thumbnail = imageService.findById(dto.getThumbnailImageId());

        Feed feed = feedRepository.save(dto.toFeedWith(user, restaurant, thumbnail));

        statisticsService.addRecentUser(user);

        feed.getOwner().feedCountUp();
        feed.getRestaurant().feedCountUp(feed);
        userLevelFeedCountService.levelFeedUp(feed);
        return feed.getId();
    }

    @Transactional
    public void updateFeed(Long feedId, FeedInfoDto dto, Long userId) {
        Feed feed = findById(feedId);
        if (!feed.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("해당 피드를 업데이트할 수 없습니다.");

        feed.getRestaurant().feedCountDown(feed);
        dto.updateSimpleAttrs(feed);
        updateRelationalAttrs(feed, dto);
        feed.getRestaurant().feedCountUp(feed);

        recommendFeedService.checkAndUpdate(feed);
    }

    private void updateRelationalAttrs(Feed feed, FeedInfoDto dto) {
        if (feed.getThumbnail() != null && !feed.getThumbnail().getId().equals(dto.getThumbnailImageId()))
            feed.setThumbnail(imageService.findById(dto.getThumbnailImageId()));

        List<Container> newMenus = dto.toContainerListWith(feed, feed.getRestaurant());
        ofNullable(dto.getRestaurantCreateDto()).ifPresentOrElse(
                restaurantDto -> feed.setRestaurant(restaurantService.findByDto(restaurantDto)),
                () -> {
                    Map<String, Container> menuMap = feed.getContainerList().stream()
                            .collect(HashMap::new, (m, c) -> m.put(c.getMenu().getName(), c), Map::putAll);
                    // 식당이 그대로인 경우 이름이 동일한 메뉴는 새 리스트에 대체
                    newMenus.replaceAll(container -> {
                        Container replace = menuMap.get(container.getMenu().getName());
                        if (replace != null) {
                            replace.setDescription(container.getDescription());
                            return replace;
                        }
                        return container;
                    });
                });
        feed.updateContainers(newMenus);
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

    private class FeedPreviewDtoAssembler implements RepresentationModelAssembler<Feed, FeedPreviewDto> {

        final Set<Long> likeIdSet;

        public FeedPreviewDtoAssembler(Long loginId, Supplier<Stream<Feed>> feedSupplier) {
            List<Long> feedIdList = feedSupplier.get()
                    .map(Feed::getId)
                    .collect(Collectors.toList());

            likeIdSet = loginId == null ? Set.of() :
                    feedLikeRepository.checkFeedLikeOnIdList(loginId, feedIdList);
        }

        @NotNull
        @Override
        public FeedPreviewDto toModel(@NotNull Feed entity) {
            return FeedPreviewDto.from(entity, likeIdSet.contains(entity.getId()));
        }
    }
}