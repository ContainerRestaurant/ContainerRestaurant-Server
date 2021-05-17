package container.restaurant.server.domain.comment;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
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
    @Autowired
    CommentService commentService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    FeedRepository feedRepository;
    @Autowired
    CommentRepository commentRepository;

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
                .image_ID(1L)
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
    @DisplayName("댓글 작성")
    void createComment() {
        // 답댓글
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", 2L);

        SessionUser sessionUser = SessionUser.from(userRepository.findById(1L).orElseThrow(()->new ResourceNotFoundException("No User")));

        CommentInfoDto dto = commentService.createComment(commentCreateDto, 2L, sessionUser.getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(1L);

        // 댓글
        CommentCreateDto commentCreateDto1 = new CommentCreateDto("test");

        CommentInfoDto dto1 = commentService.createComment(commentCreateDto1, 2L, sessionUser.getId());

        assertThat(dto1.getContent()).isEqualTo("test");
        assertThat(dto1.getOwnerId()).isEqualTo(1L);
    }
}
