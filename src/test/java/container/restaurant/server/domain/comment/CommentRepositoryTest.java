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
    protected MockHttpSession myselfSession;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;


    protected User myself;
    protected User other;

    @Test
    void findAllTest(){

//        userRepository.findAll()
//                .forEach(user -> System.out.println(user.getEmail()));
        List<User> user = userRepository.findAll();
        System.out.println(user.size());
//        List<Comment> comments = commentRepository.findAll();
        List<Feed> feed = feedRepository.findAll();
        System.out.println(feed.size());
//        System.out.println(comments.size());
    }

    @Test
    void createTest(){
        myself = User.builder()
                .email("me@test.com")
                .profile("https://my.profile.path")
                .build();
        myself.setNickname("테스트닉네임");
//        myself = userRepository.save(myself);

        myselfSession.setAttribute("user", SessionUser.from(myself));

//        Feed feed = feedRepository.getOne(Long.valueOf(5));
//        User user = userRepository.getOne(Long.valueOf(27));

        User user = User.builder()
                .email("test@test.com")
                .profile("https://my.profile")
                .build();
        String description = "description";
        Boolean welcome = true;
        Integer difficulty = 5;
        LocalDateTime now = LocalDateTime.now();

        //when
        Feed feed = Feed.builder()
                .owner(user)
                .description(description)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();

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
    void saveTest(){
        User user = User.builder()
                .email("test@test.com")
                .profile("https://test")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .build();

        Feed feed = Feed.builder()
                .owner(user)
                .restaurant(restaurant)
                .difficulty(3)
                .build();

        Comment comment = commentRepository.save(Comment.builder()
                .content("test")
                .feed(feed)
                .owner(user)
                .build()
        );

    }
    @Test
    void findCommentTest(){
        System.out.println("ASD");
        System.out.println(feedRepository.getOne(Long.valueOf(5)).getDescription());
        commentRepository.findAllByFeed(feedRepository.getOne(Long.valueOf(5)))
                .forEach(comment -> System.out.println(comment.getContent()));
    }

    @Test
    void TEST() throws Exception{
        User user = User.builder()
                .email("test@test.com")
                .profile("https://test")
                .build();
        user.setNickname("tester");
        if (!userRepository.existsUserByNickname(user.getNickname()))
            user = userRepository.save(user);

        Feed feed = feedRepository.save(Feed.builder()
                .owner(user)
                .difficulty(3)
                .build());
    }


}