package container.restaurant.server.domain.comment;

import ch.qos.logback.core.net.SyslogOutputStream;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.FeedRepositoryTest;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    void findAllTest(){
        List<Comment> comments = commentRepository.findAll();
        System.out.println(comments.size());
    }

    @Test
    void saveTest(){
        Comment comment = commentRepository.save(Comment.builder()
                .content("test")
                .feed(feedRepository.findById(Long.valueOf(5)).orElseThrow())
                .owner(userRepository.findById(Long.valueOf(27)).orElseThrow())
                .build()
        );
    }
    @Test
    void findCommentTest(){
        commentRepository.findAllByFeed(feedRepository.findById(Long.valueOf(5)).orElseThrow())
                .forEach(comment -> System.out.println(comment.getContent()));
    }
}