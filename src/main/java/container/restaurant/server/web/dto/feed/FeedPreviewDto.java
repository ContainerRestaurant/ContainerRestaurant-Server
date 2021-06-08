package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.ImageService;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/**
 * 피드 리스트를 요청한 경우, 리스트에는 각 피드에 대한 간략한 정보만 응답하기 위한 DTO
 */
@Getter
public class FeedPreviewDto extends RepresentationModel<FeedPreviewDto> {

    private final Long id;

    private final String thumbnailUrl;
    private final String ownerNickname;
    private final String content;
    private final Integer likeCount;
    private final Integer replyCount;
    private final Boolean isContainerFriendly;

    @NonNull
    public static FeedPreviewDto from(Feed feed) {

        return new FeedPreviewDto(feed);
    }

    protected FeedPreviewDto(Feed feed) {
        this.id = feed.getId();

        this.thumbnailUrl = ImageService.getUrlFromImage(feed.getThumbnail());
        this.ownerNickname = feed.getOwner().getNickname();
        this.content = feed.getContent();
        this.likeCount = feed.getLikeCount();
        this.replyCount = feed.getReplyCount();
        this.isContainerFriendly = feed.getRestaurant().isContainerFriendly();
    }

}
