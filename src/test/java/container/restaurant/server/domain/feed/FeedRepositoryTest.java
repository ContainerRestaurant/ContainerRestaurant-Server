package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FeedRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Test
    void 이거는_테스트입니다() throws Exception {
        assert userRepository != null;
        assert feedRepository != null;

        userRepository.findAll()
                .forEach(user -> System.out.println(user.getEmail()));

        User user = User.builder()
                .email("test@test.com")
                .profile("https://test")
                .build();
        user.setNickname("tester");
        if (!userRepository.existsUserByNickname(user.getNickname()))
            user = userRepository.save(user);
//
//        Restaurant restaurant = restaurantRepository.findById(1L)
//                .orElseThrow(() -> {throw new RuntimeException();});
//
//        Feed feed = feedRepository.save(Feed.builder()
//                .difficulty(2)
//                .owner(user)
//                .restaurant(restaurant)
//                .category(Category.KOREAN)
//                .build());
    }

}