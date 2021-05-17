package container.restaurant.server.domain.comment;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
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
class CommentServiceTest {
    private static final Category[] CATEGORY_ARR = {
            Category.KOREAN, Category.JAPANESE, Category.INSTANT
    };

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    protected List<User> users;
    protected List<Restaurant> restaurants;
    protected List<Feed> feeds;
    protected List<Comment> comments;

    @BeforeEach
    void beforeEach() {
        // 3명의 유저
        users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(User.builder()
                    .email("me" + i + "@test.com")
                    .profile("https://my" + i + ".profile.path")
                    .nickname("TestNickname" + i)
                    .build());
        }
        users = userRepository.saveAll(users);

        Image image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        // 5개의 식당
        restaurants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            restaurants.add(restaurantRepository.save(Restaurant.builder()
                    .name("restaurant")
                    .addr("address")
                    .lat(1f)
                    .lon(1f)
                    .image_ID(image.getId())
                    .build()));
        }
        restaurants = restaurantRepository.saveAll(restaurants);

        // 15 개의 피드
        feeds = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            feeds.add(feedRepository.save(Feed.builder()
                    .owner(users.get(i % 3))
                    .restaurant(restaurants.get(i % 5))
                    // 식당과 서로소가 되도록 1~4를 사용
                    .difficulty(i % 4 + 1)
                    .content("Feed Content" + i)
                    .category(CATEGORY_ARR[i % 3])
                    .build()));
        }

        comments = new ArrayList<>();
        for(int i=0; i<10; i++){
            comments.add(commentRepository.save(Comment.builder()
                .owner(users.get(i%3))
                .feed(feeds.get(i%3))
                .content("test")
                .build()
            ));
        }
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성")
    void createComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("test");

        CommentInfoDto dto = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
    }

    @Test
    @DisplayName("대댓글 작성")
    void createReplyComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", comments.get(0).getId());

        CommentInfoDto dto = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
    }
}
