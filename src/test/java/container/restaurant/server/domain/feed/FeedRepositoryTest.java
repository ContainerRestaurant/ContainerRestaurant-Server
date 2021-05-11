package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.base.BaseEntity;
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

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FeedRepositoryTest {

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    protected User myself;
    protected User other;
    protected Restaurant restaurant;
    protected Long lastIndex;

    @BeforeEach
    void beforeEach() {
        myself = User.builder()
                .email("me@test.com")
                .profile("https://my.profile.path")
                .build();
        myself.setNickname("테스트닉네임");
        myself = userRepository.save(myself);

        other = userRepository.save(User.builder()
                .email("you@test.com")
                .profile("https://your.profile.path")
                .build());

        restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lat(1f)
                .lon(1f)
                .build());

        lastIndex = feedRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(operand -> Feed.builder()
                        .owner(operand <= 4 ? other : myself)
                        .restaurant(restaurant)
                        .difficulty(2)
                        .content("content-" + operand)
                        .category(Category.KOREAN)
                        .build())
                .collect(Collectors.toList()))
                .stream()
                .map(BaseEntity::getId)
                .max(Long::compare)
                .orElse(-1L);
    }

    @AfterEach
    void afterEach() {
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("식당으로 피드 찾기")
    void testFindAllByRestaurant() {
        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(Sort.by(Sort.Order.desc("id")));
        when(pageable.getPageNumber()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(3);

        Page<Feed> result = feedRepository.findAllByRestaurant(restaurant, pageable);

        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).anyMatch(feed -> feed.getId().equals(lastIndex));
    }

    @Test
    @DisplayName("작성자로 피드 찾기")
    void testFindAllById() {
        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(Sort.by(Sort.Order.desc("id")));
        when(pageable.getPageNumber()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(2);

        Page<Feed> result = feedRepository.findAllByOwner(myself, pageable);

        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getContent()).anyMatch(feed -> feed.getId().equals(lastIndex));
    }

}
