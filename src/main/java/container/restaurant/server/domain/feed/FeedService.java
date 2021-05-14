package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;

    public FeedDetailDto getFeedDetail(Long feedId) {
        return FeedDetailDto.from(findById(feedId));
    }

    public Feed findById(Long id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 피드입니다..(id:" + id + ")"));
    }
}
