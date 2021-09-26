package container.restaurant.server.domain.feed.like;

import container.restaurant.server.BaseDataJpaTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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

    @Test
    @DisplayName("주어진 피드 ID 중에 좋아요한 ID 필터링")
    void checkFeedLikeOnIdList() {
        //given 4 개의 피드가 주어졌을 때, 1,3 번째 피드만 좋아요하면
        User user = newUser();
        Restaurant restaurant = newRestaurant();

        Feed feed1 = newFeed(user, restaurant);
        Feed feed2 = newFeed(user, restaurant);
        Feed feed3 = newFeed(user, restaurant);
        Feed feed4 = newFeed(user, restaurant);

        em.persist(FeedLike.of(user, feed1));
        em.persist(FeedLike.of(user, feed3));

        //when 주어진 피드 ID 리스트를 이용해 주어진 유저가 checkFeedLikeOnIdList() 콜하면
        List<Long> feedIdList = List.of(
                feed1.getId(), feed2.getId(), feed3.getId(), feed4.getId());
        Set<Long> result = feedLikeRepository.checkFeedLikeOnIdList(user.getId(), feedIdList);

        //then 1, 3 번째 피드 ID 두 개 만을 포함하고 있음
        assertThat(result)
                .hasSize(2)
                .contains(feed1.getId(), feed3.getId())
                .doesNotContain(feed2.getId(), feed4.getId());
    }

}