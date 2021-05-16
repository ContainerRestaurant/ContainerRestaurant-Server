package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final ScrapFeedRepository scrapFeedRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    private final PagedResourcesAssembler<Feed> feedAssembler;
    private final PagedResourcesAssembler<ScrapFeed> scrapAssembler;

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
    public PagedModel<FeedPreviewDto> findAll(Pageable pageable) {
        return feedAssembler.toModel(
                feedRepository.findAll(pageable), FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUser(Long userId, Pageable pageable) {
        return feedAssembler.toModel(
                feedRepository.findAllByOwnerId(userId, pageable), FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByRestaurant(Long restaurantId, Pageable pageable) {
        return feedAssembler.toModel(
                feedRepository.findAllByRestaurantId(restaurantId, pageable), FeedPreviewDto::from);
    }

    @Transactional(readOnly = true)
    public PagedModel<FeedPreviewDto> findAllByUserScrap(Long userId, Pageable pageable) {
        return scrapAssembler.toModel(
                scrapFeedRepository.findAllByUserId(userId, pageable), FeedPreviewDto::from);
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
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 사용자입니다.(id:" + ownerId + ")"));
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 식당입니다.(id:" + dto.getRestaurantId() + ")"));
        return feedRepository.save(dto.toEntityWith(user, restaurant))
                .getId();
    }
}
