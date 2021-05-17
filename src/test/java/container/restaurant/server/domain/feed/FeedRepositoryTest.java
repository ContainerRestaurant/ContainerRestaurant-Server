package container.restaurant.server.domain.feed;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FeedRepositoryTest {

    private static final Category[] CATEGORY_ARR = {
            Category.KOREAN, Category.JAPANESE, Category.NIGHT_MEAL
    };

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ImageRepository imageRepository;

    protected List<User> users;
    protected List<Restaurant> restaurants;
    protected List<Feed> feeds;

    @BeforeEach
    void beforeEach() throws InterruptedException {
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
            // 피드 생성시간 차이를 위해 잠시 대기
            Thread.sleep(0, 1);
        }
    }

    @AfterEach
    void afterEach() {
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("식당으로 피드 찾기")
    void testFindAllByRestaurant() {
        //given 식당과 난이도 순으로 정렬한 2-pageable 이 주어졌을 때
        Restaurant restaurant = restaurants.get(0);

        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(Sort.by(Sort.Order.desc("difficulty")));
        when(pageable.getPageSize()).thenReturn(2);

        //when 식당과 pageable 을 이용해 피드를 조회하면
        Page<Feed> result = feedRepository.findAllByRestaurantId(restaurant.getId(), pageable);
        List<Feed> list = result.getContent();

        //then-1 식당은 3개의 피드를 가지고 있고, 2개씩 두 페이지가 된다.
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);

        //then-2 난이도 순으로 정렬되어 나온다.
        assertThat(list.get(0).getDifficulty()).isGreaterThan(list.get(1).getDifficulty());
    }

    @Test
    @DisplayName("작성자로 피드 찾기")
    void testFindAllById() {
        //given 작성자와 최신순으로 정렬한 3-pageable 이 주어졌을 때
        User me = users.get(0);

        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(Sort.by(Sort.Order.desc("createdDate")));
        when(pageable.getPageSize()).thenReturn(3);

        //when 작성자와 pageable 을 이용해 피드를 조회하면
        Page<Feed> result = feedRepository.findAllByOwnerId(me.getId(), pageable);
        List<Feed> list = result.getContent();

        //then-1 작성자는 5개의 피드를 가지고 있고, 2개씩 총 세 페이지가 된다.
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);

        //then-2 최신 순으로 정렬되어 나온다.
        assertThat(list.get(0).getCreatedDate()).isAfter(list.get(1).getCreatedDate());
    }

}
