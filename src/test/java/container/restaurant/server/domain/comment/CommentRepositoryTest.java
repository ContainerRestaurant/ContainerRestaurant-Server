package container.restaurant.server.domain.comment;

import ch.qos.logback.core.net.SyslogOutputStream;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.FeedRepositoryTest;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllTest(){
        assert userRepository != null;
        assert feedRepository != null;
        userRepository.findAll()
                .forEach(user -> System.out.println(user.getEmail()));
        commentRepository.findAll()
                .forEach(comment -> System.out.println(comment.getContent()));
        List<User> user = userRepository.findAll();
        System.out.println(user.size());
    }

    @Test
    void createTest(){
        Feed feed = feedRepository.getOne(Long.valueOf(5));
        User user = userRepository.getOne(Long.valueOf(27));

        assertNotNull(feed);
        assertNotNull(user);

        Comment comment = Comment.builder()
                .content("test")
                .feed(feed)
                .owner(user)
                .build();
        System.out.println(feed.getDescription());
        comment = commentRepository.save(comment);
    }

    @Test
    void findCommentTest(){
        System.out.println("ASD");
        commentRepository.findAllByFeed(feedRepository.getOne(Long.valueOf(5)))
                .forEach(comment -> System.out.println("comment.getContent()"));
    }

    @Test
    void TEST() throws Exception{

//        Feed feed = feedRepository.findById(Long.valueOf(5))
//                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 피드입니다.(id:5)"));
//        User user = userRepository.findById(Long.valueOf(27))
//                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 유저입니다.(id:27)"));
//
//        Comment comment = Comment.builder()
//                .feed(feed)
//                .owner(user)
//                .content("테스트")
//                .build();
//        comment = commentRepository.save(comment);
//        System.out.println(comment);

//        Comment comment = commentRepository.findById(Long.valueOf(6))
//                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글"));
//        System.out.println(comment);
        assert false;

    }


}