package container.restaurant.server.domain.feed.like;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.AuthProvider;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FeedLikeRepositoryTest {

    @Autowired
    FeedLikeRepository feedLikeRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ImageRepository imageRepository;

    @AfterEach
    void afterEach() {
        feedLikeRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void testExistsByUserAndFeed() {
        //given: user 와 feed 가 주어졌을 때
        Image image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        User user = userRepository.save(User.builder()
                .authId("authId")
                .authProvider(AuthProvider.KAKAO)
                .email("test@test.com")
                .nickname("testNickname")
                .profile(image)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .thumbnail(image)
                .build());

        Feed feed = feedRepository.save(Feed.builder()
                .owner(user)
                .restaurant(restaurant)
                .difficulty(3)
                .category(Category.KOREAN)
                .build());

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