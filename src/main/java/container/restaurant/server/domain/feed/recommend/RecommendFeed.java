package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.utils.ImageUtils;
import lombok.Getter;

import java.util.Objects;

/**
 * 추천 피드를 캐싱하기 위한 빈 클래스</br>
 * {@link container.restaurant.server.web.dto.feed.FeedPreviewDto}에서 현재 유저와 관련된 정보가 빠진 형태다.
 */
@Getter
public class RecommendFeed {

    private Long id;
    private String thumbnailUrl;
    private String ownerNickname;
    private String content;
    private Integer likeCount;
    private Integer replyCount;

    public RecommendFeed(Feed feed) {
        setValues(feed);
    }

    public void update(Feed feed) {
        if (feed == null || !Objects.equals(this.id, feed.getId()))
            return;

        setValues(feed);
    }

    private void setValues(Feed feed) {
        this.id = feed.getId();
        this.thumbnailUrl = ImageUtils.getUrlFromImage(feed.getThumbnail());
        this.ownerNickname = feed.getOwner().getNickname();
        this.content = feed.getContent();
        this.likeCount = feed.getLikeCount();
        this.replyCount = feed.getReplyCount();
    }
}
