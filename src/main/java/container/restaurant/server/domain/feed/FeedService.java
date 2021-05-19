package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;

    private final PagedResourcesAssembler<Feed> feedAssembler;

    @Transactional(readOnly = true)
    public FeedDetailDto getFeedDetail(Long feedId) {

        return FeedDetailDto.from(findById(feedId));
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
        feedRepository.delete(feed);
    }

    @Transactional
    public Long createFeed(FeedInfoDto dto, Long ownerId) {
        User user = userService.findById(ownerId);
        Restaurant restaurant = restaurantService.findById(dto.getRestaurantId());
        return feedRepository.save(dto.toEntityWith(user, restaurant))
                .getId();
    }

    @Transactional
    public void updateFeed(Long feedId, FeedInfoDto dto, Long userId) {
        Feed feed = findById(feedId);
        if (!feed.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("해당 피드를 업데이트할 수 없습니다.");

        Restaurant restaurant = restaurantService.findById(dto.getRestaurantId());
        if (restaurant != null)
            feed.setRestaurant(restaurant);
        dto.update(feed);
    }

    @Transactional
    public List<Feed> findByLatestFeedUsers() {
        return feedRepository.findByLatestFeed();
    }
}
