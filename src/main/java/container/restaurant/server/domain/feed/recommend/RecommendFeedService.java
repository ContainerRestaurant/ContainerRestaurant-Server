package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendFeedService {

    private CollectionModel<FeedPreviewDto> recommendFeeds = CollectionModel.of(List.of());

    public CollectionModel<FeedPreviewDto> getRecommendFeeds() {
        return recommendFeeds;
    }

    public void updateRecommendFeed(List<Feed> list) {
        recommendFeeds = CollectionModel.of(list.stream().map(FeedPreviewDto::from).collect(Collectors.toList()));
    }

}
