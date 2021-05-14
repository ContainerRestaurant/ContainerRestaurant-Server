package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;


    protected User user;
    protected Restaurant restaurant;
    List<Feed> feeds = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .profile("https://test")
                .build());

        restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .build());

        for (int i = 0; i < 3; i++) {
            feeds.add(feedRepository.save(Feed.builder()
                    .owner(user)
                    .restaurant(restaurant)
                    .difficulty(3)
                    .category(Category.KOREAN)
                    .build()));
        }

        for (int i = 0; i < 10; i++) {
            comments.add(commentRepository.save(Comment.builder()
                .content("test")
                .feed(feeds.get(i % 3))
                .owner(user)
                .build()));
        }
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 댓글 조회")
    void findAllTest(){
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void saveTest() {
        Comment comment = commentRepository.save(Comment.builder()
                .content("test")
                .feed(feeds.get(0))
                .owner(user)
                .build()
        );

        commentRepository.findById(comment.getId())
                .orElseThrow(() -> {throw new RuntimeException();});
    }

    @Test
    @DisplayName("하나 이상의 댓글이 조회된다.")
    void findCommentTest(){
        List<Comment> list = commentRepository.findAllByFeed(feeds.get(0));
        assertThat(list.size()).isGreaterThan(0);
    }

}