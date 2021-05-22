package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentLikeServiceTest {

    @Autowired
    CommentLikeService commentLikeService;

    @Autowired
    CommentLikeRepository commentLikeRepository;

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

    @Autowired
    CommentRepository commentRepository;

    protected Comment comment;
    protected User user;
    protected Image image;
    protected Restaurant restaurant;
    protected Feed feed;

    @BeforeEach
    void beforeEach(){

        user = userRepository.save(User.builder()
                .email("test@test.com")
                .profile("https://my.profile")
                .build());

        image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .image_ID(image.getId())
                .build());

        feed = feedRepository.save(Feed.builder()
                .owner(user)
                .restaurant(restaurant)
                .difficulty(3)
                .category(Category.KOREAN)
                .build());

        comment = commentRepository.save(Comment.builder()
                .content("TEST")
                .owner(user)
                .feed(feed)
                .build());
    }

    @AfterEach
    void afterEach(){
        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 좋아요")
    void userLikeComment() {
        // 댓글 좋아요 1번
        commentLikeService.userLikeComment(user.getId(), comment.getId());
        // 댓글 좋아요 2번 (아무런 일이 일어나지 않음)
        commentLikeService.userLikeComment(user.getId(), comment.getId());

        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        assertThat(commentLikes).isNotNull();
        assertThat(commentLikes.size()).isEqualTo(1);

        // 댓글 좋아요 취소 1번
        commentLikeService.userCancelLikeComment(user.getId(), comment.getId());
        // 댓글 좋아요 취소 2번 (아무런 일이 일어나지 않음)
        commentLikeService.userCancelLikeComment(user.getId(), comment.getId());

        commentLikes = commentLikeRepository.findAll();
        assertThat(commentLikes.size()).isEqualTo(0);
    }
}