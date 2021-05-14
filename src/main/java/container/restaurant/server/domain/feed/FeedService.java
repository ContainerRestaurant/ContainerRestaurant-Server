package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    private final PagedResourcesAssembler<Feed> assembler;

    public FeedDetailDto getFeedDetail(Long feedId) {

        return FeedDetailDto.from(findById(feedId));
    }

    public Feed findById(Long id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 피드입니다..(id:" + id + ")"));
    }

    public PagedModel<FeedPreviewDto> findAll(Pageable pageable) {
        return assembler.toModel(
                feedRepository.findAll(pageable), FeedPreviewDto::from);
    }

    public PagedModel<FeedPreviewDto> findAllByUser(Long userId, Pageable pageable) {
        return assembler.toModel(
                feedRepository.findAllByOwnerId(userId, pageable), FeedPreviewDto::from);
    }

    public PagedModel<FeedPreviewDto> findAllByRestaurant(Long restaurantId, Pageable pageable) {
        return assembler.toModel(
                feedRepository.findAllByRestaurantId(restaurantId, pageable), FeedPreviewDto::from);
    }
}
