package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 단일 피드에 대한 상세 정보를 응답하기 위한 DTO
 */
@Getter
public class FeedDetailDto extends RepresentationModel<FeedDetailDto> {

    private final Long id;
    private final Long ownerId;
    private final Long restaurantId;

    private final String ownerNickname;
    private final String restaurantName;
    private final Category category;
    private final List<String> imageUrls;
    private final String content;
    private final Boolean welcome;
    private final Integer difficulty;
    private final Integer likeCount;
    private final Integer scrapCount;
    private final Integer replyCount;

    public static FeedDetailDto from(Feed feed, List<Image> image) {
        return new FeedDetailDto(feed, image);
    }

    private FeedDetailDto(Feed feed, List<Image> images) {
        this.id = feed.getId();
        this.ownerId = feed.getOwner().getId();
        this.restaurantId = feed.getRestaurant().getId();

        this.ownerNickname = feed.getOwner().getNickname();
        this.restaurantName = feed.getRestaurant().getName();
        this.category = feed.getCategory();
        this.imageUrls = images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
        this.content = feed.getContent();
        this.welcome = feed.getWelcome();
        this.difficulty = feed.getDifficulty();
        this.likeCount = feed.getLikeCount();
        this.scrapCount = feed.getScrapedCount();
        this.replyCount = feed.getReplyCount();
    }

}
