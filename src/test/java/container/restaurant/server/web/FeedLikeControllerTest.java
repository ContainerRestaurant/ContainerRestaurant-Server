package container.restaurant.server.web;

import container.restaurant.server.domain.feed.like.FeedLike;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FeedLikeControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    private FeedLikeRepository feedLikeRepository;

    @Override
    @AfterEach
    public void afterEach() {
        feedLikeRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("피드 좋아요")
    void testUserLikeFeed() throws Exception {
        //given 초기 FeeLike 사이즈가 주어졌을 때
        int size = feedLikeRepository.findAllByFeed(othersFeed).size();

        //when myself 유저 세션으로 주어진 피드를 좋아요 하면
        mvc.perform(
                post("/api/like/feed/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, cancel-like 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.cancel-like.href").exists());

        //then FeedLike 가 하나 증가하고, 관계가 잘 정의되어있다.
        List<FeedLike> likeList = feedLikeRepository.findAllByFeed(othersFeed);
        assertThat(likeList.size()).isEqualTo(size + 1);

        FeedLike like = likeList.get(0);
        assertThat(like.getFeed().getId()).isEqualTo(othersFeed.getId());
        assertThat(like.getUser().getId()).isEqualTo(myself.getId());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("이미 좋아요한 피드 좋아요 - 아무일 엄슴")
    void testUserLikeFeedExists() throws Exception {
        //given otherFeed 를 좋아요한 myself 가 주어졌을 때
        feedLikeRepository.save(FeedLike.of(myself, othersFeed));
        myself = userRepository.findById(myself.getId())
                .orElse(myself);
        Integer size = feedLikeRepository.findAllByFeed(othersFeed).size();

        //when myself 유저 세션으로 해당 피드를 다시 좋아요 하면
        mvc.perform(
                post("/api/like/feed/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, cancel-like 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.cancel-like.href").exists());

        //then FeedLike 사이즈가 변함없고, 관계가 잘 정의되어있다.
        List<FeedLike> likeList = feedLikeRepository.findAllByFeed(othersFeed);
        assertThat(likeList.size()).isEqualTo(size);

        FeedLike like = likeList.get(0);
        assertThat(like.getFeed().getId()).isEqualTo(othersFeed.getId());
        assertThat(like.getUser().getId()).isEqualTo(myself.getId());
    }

    @Test
    @DisplayName("인증되지 않은 유저의 좋아요 실패")
    void failUnauthenticatedUser() throws Exception {
        //given other 유저가 작성한 피드가 주어졌을 때

        //then 인증되지 않은 유저가 주어진 피드를 스크랩하면 login 리다이렉트
        mvc.perform(post("/api/like/feed/{feedId}", othersFeed.getId()))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("피드 좋아요 취소")
    void testUserCancelLikeFeed() throws Exception {
        //given otherFeed 를 좋아요한 myself 가 주어졌을 때
        feedLikeRepository.save(FeedLike.of(myself, othersFeed));
        myself = userRepository.findById(myself.getId())
                .orElse(myself);
        int size = feedLikeRepository.findAllByFeed(othersFeed).size();

        //when myself 유저 세션으로 주어진 피드를 좋아요 하면
        mvc.perform(
                delete("/api/like/feed/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, cancel-like 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.like.href").exists());

        //then FeedLike 가 하나 감소한다
        List<FeedLike> likeList = feedLikeRepository.findAllByFeed(othersFeed);
        assertThat(likeList.size()).isEqualTo(size - 1);
    }

}
