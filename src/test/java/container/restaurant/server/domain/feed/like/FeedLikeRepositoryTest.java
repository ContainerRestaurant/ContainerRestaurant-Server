package container.restaurant.server.domain.feed.like;

import container.restaurant.server.BaseDataJpaTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeedLikeRepositoryTest extends BaseDataJpaTest {

    @Autowired
    FeedLikeRepository feedLikeRepository;

    @Test
    @DisplayName("유저의 피드 좋아요 여부 확인 테스트")
    void testExistsByUserAndFeed() {
        //given: user 와 feed 가 주어졌을 때
        User user = newUser();
        Restaurant restaurant = newRestaurant();

        Feed feed = newFeed(user, restaurant);

        //when: 둘 사이에 FeedLike 을 추가하면
        feedLikeRepository.save(FeedLike.of(user, feed));

        //then-1: feed 를 이용해 좋아요를 찾을 수 있다.
        List<FeedLike> feedLikes = feedLikeRepository.findAllByFeed(feed);
        assertThat(feedLikes).isNotNull();
        assertThat(feedLikes.get(0).getFeed().getId()).isEqualTo(feed.getId());
        assertThat(feedLikes.get(0).getUser().getId()).isEqualTo(user.getId());

        //then-2: 유저와 피드로 해당 스크랩을 찾을 수 있다.
        FeedLike scrap = feedLikeRepository.findByUserAndFeed(user, feed)
                .orElse(null);
        assertThat(scrap).isNotNull();
        assertThat(scrap.getFeed().getId()).isEqualTo(feed.getId());
        assertThat(scrap.getUser().getId()).isEqualTo(user.getId());
    }

}