package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.feed.recommend.RecommendFeed;
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
    private final Boolean isLike;

    @NonNull
    public static FeedPreviewDto from(Feed feed, Boolean isLike) {
        return new FeedPreviewDto(
                feed.getId(), ImageService.getUrlFromImage(feed.getThumbnail()),
                feed.getOwner().getNickname(), feed.getContent(), feed.getLikeCount(),
                feed.getReplyCount(), isLike);
    }

    @NonNull
    public static FeedPreviewDto from(RecommendFeed recommendFeed, Boolean isLike) {
        return new FeedPreviewDto(recommendFeed.getId(), recommendFeed.getThumbnailUrl(),
                recommendFeed.getOwnerNickname(), recommendFeed.getContent(), recommendFeed.getLikeCount(),
                recommendFeed.getReplyCount(), isLike);
    }

    private FeedPreviewDto(Long id, String thumbnailUrl, String ownerNickname, String content, Integer likeCount,
                           Integer replyCount, Boolean isLike
    ) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.ownerNickname = ownerNickname;
        this.content = content;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.isLike = isLike;
    }

}
