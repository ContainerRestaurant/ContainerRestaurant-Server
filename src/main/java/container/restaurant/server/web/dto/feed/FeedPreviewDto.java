package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Feed;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

/**
 * 피드 리스트를 요청한 경우, 리스트에는 각 피드에 대한 간략한 정보만 응답하기 위한 DTO
 */
@Getter
public class FeedPreviewDto extends RepresentationModel<FeedPreviewDto> {

    private final Long id;

    private final String thumbnailUrl;
    private final String nickname;
    private final String content;
    private final Integer likeCount;
    private final Integer replyCount;

    public static FeedPreviewDto from(Feed feed) {
        return new FeedPreviewDto(feed);
    }

    protected FeedPreviewDto(Feed feed) {
        this.id = feed.getId();

        this.thumbnailUrl = feed.getThumbnailUrl();
        this.nickname = feed.getOwner().getNickname();
        this.content = feed.getContent();
        this.likeCount = feed.getLikeCount();
        this.replyCount = feed.getReplyCount();
    }

}
