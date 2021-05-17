package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    private final PagedResourcesAssembler<Feed> feedAssembler;

    public FeedDetailDto getFeedDetail(Long feedId) {

        return FeedDetailDto.from(findById(feedId));
    }

    public Feed findById(Long id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 피드입니다..(id:" + id + ")"));
    }

    public PagedModel<FeedPreviewDto> findAll(Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByCategory(pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAll(pageable)),
                FeedPreviewDto::from);
    }

    public PagedModel<FeedPreviewDto> findAllByUser(
            Long userId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByOwnerIdAndCategory(
                                userId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByOwnerId(userId, pageable)),
                FeedPreviewDto::from);
    }

    public PagedModel<FeedPreviewDto> findAllByRestaurant(
            Long restaurantId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByRestaurantIdAndCategory(
                                restaurantId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByRestaurantId(restaurantId, pageable)),
                FeedPreviewDto::from);
    }

    public PagedModel<FeedPreviewDto> findAllByUserScrap(
            Long userId, Pageable pageable, Category categoryFilter) {
        return feedAssembler.toModel(
                ofNullable(categoryFilter)
                        .map(category -> feedRepository.findAllByScraperIdAndCategory(
                                userId, pageable, categoryFilter))
                        .orElseGet(() -> feedRepository.findAllByScraperId(userId, pageable)),
                FeedPreviewDto::from);
    }
}
