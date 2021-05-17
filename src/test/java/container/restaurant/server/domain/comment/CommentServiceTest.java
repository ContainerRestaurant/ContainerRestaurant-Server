package container.restaurant.server.domain.comment;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    @DisplayName("답댓글 작성")
    void createReplyComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", 2L);

        SessionUser sessionUser = SessionUser.from(userRepository.findById(1L).orElseThrow(()->new ResourceNotFoundException("No User")));

        CommentInfoDto dto = commentService.createComment(commentCreateDto, 2L, sessionUser.getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("댓글 작성")
    void createComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("test");

        SessionUser sessionUser = SessionUser.from(userRepository.findById(1L).orElseThrow(()->new ResourceNotFoundException("No User")));

        CommentInfoDto dto = commentService.createComment(commentCreateDto, 2L, sessionUser.getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(1L);
    }
}
