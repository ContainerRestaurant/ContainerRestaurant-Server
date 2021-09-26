package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RecommendFeedTest {

    int i = 0;

    @Test
    @DisplayName("추천 피드 생성 테스트")
    void constructor() {
        //given 추천 피드 관련 정보가 모킹된 피드가 주어졌을 때
        Feed feed = newFeed(1L);

        //when
        RecommendFeed result = new RecommendFeed(feed);

        //then
        assertThat(result.getId()).isEqualTo(feed.getId());
        assertThat(result.getThumbnailUrl())
                .isNotNull()
                .isEqualTo(ImageService.getUrlFromImage(feed.getThumbnail()));
        assertThat(result.getOwnerNickname())
                .isNotNull()
                .isEqualTo(feed.getOwner().getNickname());
        assertThat(result.getContent()).isEqualTo(feed.getContent());
        assertThat(result.getLikeCount()).isEqualTo(feed.getLikeCount());
        assertThat(result.getReplyCount()).isEqualTo(feed.getReplyCount());
        assertThat(result.getIsContainerFriendly()).isEqualTo(feed.getRestaurant().isContainerFriendly());
    }

    @Test
    @DisplayName("추천 피드 업데이트 테스트 - 동일한 ID")
    void update() {
        //given
        Feed origin = newFeed(1L);
        Feed to = newFeed(1L);

        RecommendFeed recommendFeed = new RecommendFeed(origin);

        //when
        recommendFeed.update(to);

        //then
        assertThat(recommendFeed.getId()).isEqualTo(to.getId());
        assertThat(recommendFeed.getThumbnailUrl())
                .isNotNull()
                .isEqualTo(ImageService.getUrlFromImage(to.getThumbnail()));
        assertThat(recommendFeed.getOwnerNickname())
                .isNotNull()
                .isEqualTo(to.getOwner().getNickname());
        assertThat(recommendFeed.getContent()).isEqualTo(to.getContent());
        assertThat(recommendFeed.getLikeCount()).isEqualTo(to.getLikeCount());
        assertThat(recommendFeed.getReplyCount()).isEqualTo(to.getReplyCount());
        assertThat(recommendFeed.getIsContainerFriendly()).isEqualTo(to.getRestaurant().isContainerFriendly());
    }

    @Test
    @DisplayName("추천 피드 업데이트 테스트 - 서로 다른 ID")
    void update_differentId() {
        //given
        Feed origin = newFeed(1L);
        Feed to = newFeed(2L);

        RecommendFeed recommendFeed = new RecommendFeed(origin);

        //when
        recommendFeed.update(to);

        //then
        assertThat(recommendFeed.getId()).isEqualTo(origin.getId());
        assertThat(recommendFeed.getThumbnailUrl())
                .isNotNull()
                .isEqualTo(ImageService.getUrlFromImage(origin.getThumbnail()));
        assertThat(recommendFeed.getOwnerNickname())
                .isNotNull()
                .isEqualTo(origin.getOwner().getNickname());
        assertThat(recommendFeed.getContent()).isEqualTo(origin.getContent());
        assertThat(recommendFeed.getLikeCount()).isEqualTo(origin.getLikeCount());
        assertThat(recommendFeed.getReplyCount()).isEqualTo(origin.getReplyCount());
        assertThat(recommendFeed.getIsContainerFriendly()).isEqualTo(origin.getRestaurant().isContainerFriendly());
    }

    @Test
    @DisplayName("추천 피드 업데이트 테스트 - null")
    void update_null() {
        //given
        Feed origin = newFeed(1L);

        RecommendFeed recommendFeed = new RecommendFeed(origin);

        //when
        recommendFeed.update(null);

        //then
        assertThat(recommendFeed.getId()).isEqualTo(origin.getId());
        assertThat(recommendFeed.getThumbnailUrl())
                .isNotNull()
                .isEqualTo(ImageService.getUrlFromImage(origin.getThumbnail()));
        assertThat(recommendFeed.getOwnerNickname())
                .isNotNull()
                .isEqualTo(origin.getOwner().getNickname());
        assertThat(recommendFeed.getContent()).isEqualTo(origin.getContent());
        assertThat(recommendFeed.getLikeCount()).isEqualTo(origin.getLikeCount());
        assertThat(recommendFeed.getReplyCount()).isEqualTo(origin.getReplyCount());
        assertThat(recommendFeed.getIsContainerFriendly()).isEqualTo(origin.getRestaurant().isContainerFriendly());
    }

    @NotNull
    private Feed newFeed(long id) {
        i++;

        Image image = mock(Image.class);
        when(image.getUrl()).thenReturn("IMAGE URL" + i);

        User owner = mock(User.class);
        when(owner.getNickname()).thenReturn("NICKNAME" + i);

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isContainerFriendly()).thenReturn(i % 2 == 0);

        Feed feed = spy(Feed.builder()
                .thumbnail(image)
                .owner(owner)
                .restaurant(restaurant)
                .content("CONTENT" + i)
                .build());
        when(feed.getId()).thenReturn(id);
        when(feed.getLikeCount()).thenReturn(i * 2);
        when(feed.getReplyCount()).thenReturn(i * 3);
        return feed;
    }

}
