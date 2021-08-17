package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.OAuth2Identifier;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Pageable.unpaged;

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
    ScrapFeedRepository scrapFeedRepository;

    @Autowired
    ImageRepository imageRepository;

    protected List<User> users;
    protected List<Restaurant> restaurants;
    protected List<Feed> feeds;

    @BeforeEach
    void beforeEach() throws InterruptedException {

        Image image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        // 3명의 유저
        users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(User.builder()
                    .identifier(OAuth2Identifier.of("authId" + i, OAuth2Registration.KAKAO))
                    .email("me" + i + "@test.com")
                    .profile(image)
                    .nickname("TestNickname" + i)
                    .build());
        }
        users = userRepository.saveAll(users);

        // 5개의 식당
        restaurants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
             restaurants.add(restaurantRepository.save(Restaurant.builder()
                    .name("restaurant")
                    .addr("address")
                    .lat(1f)
                    .lon(1f)
                    .thumbnail(image)
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
        scrapFeedRepository.deleteAll();
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

    @Test
    @DisplayName("스크랩한 피드 찾기")
    void testScrapedFeed() {
        //given 각각의 유저가 3개씩 스크랩 했을 때
        for (int i = 0; i < 3; i++) {
            for (int o = 0; o < 3; o++) {
                scrapFeedRepository.save(ScrapFeed.of(users.get(i), feeds.get(i + o)));
            }
        }

        //when 두 번째 유저의 스크랩 피드를 조회하면
        List<Feed> res = feedRepository.findAllByScraperId(users.get(1).getId(), unpaged()).getContent();

        //then 3 개가 조회되고, 2, 3, 4 번째 피드다.
        assertThat(res.size()).isEqualTo(3);
        assertThat(res.get(0).getId()).isEqualTo(feeds.get(1).getId());
        assertThat(res.get(1).getId()).isEqualTo(feeds.get(2).getId());
        assertThat(res.get(2).getId()).isEqualTo(feeds.get(3).getId());
    }

    @Test
    @DisplayName("스크랩한 피드 카테고리 필터링 찾기")
    void testScrapedFeedWithCategory() {
        //given 각각의 유저가 3개씩 스크랩 했을 때
        for (int i = 0; i < 3; i++) {
            for (int o = 0; o < 3; o++) {
                scrapFeedRepository.save(ScrapFeed.of(users.get(i), feeds.get(i + o)));
            }
        }

        //when 두 번째 유저의 스크랩 피드를 조회하면
        List<Feed> res = feedRepository.findAllByScraperIdAndCategory(
                        users.get(1).getId(), unpaged(), CATEGORY_ARR[0])
                .getContent();

        //then 1 개가 조회되고, 필터링한 카테고리를 갖는다.
        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0).getCategory()).isEqualTo(CATEGORY_ARR[0]);
    }

    @Test
    @DisplayName("추천 피드를 위한 조회 테스트")
    void testFindRecommendFeed() throws Exception {
        //given 10 일 동안 매일 00:00:000 에 생성된 피드들이 주어졌을 때
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(1);
        LocalDateTime to = from.plusDays(9);
        Pageable pageable = PageRequest.of(0, 3);

        List<Feed> list = testFeed(from, to);
        assertThat(list.size()).isEqualTo(10);

        //expect 3 페이지씩 가져오면 9, 6, 3, 0 번째로 생성된 피드가 차례로 포함되어 있다.
        Page<Feed> page = feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, pageable);
        int i = 9;
        while (page.hasContent()) {
            assertThat(page.getContent()).contains(list.get(i));
            i -= 3;
            pageable = pageable.next();
            page = feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, pageable);
        }
    }

    List<Feed> testFeed(LocalDateTime testDateFrom, LocalDateTime testDateTo) throws Exception {
        Field createdDate_f = BaseCreatedTimeEntity.class.getDeclaredField("createdDate");
        createdDate_f.setAccessible(true);
        List<Feed> list = new ArrayList<>();

        for (int i = 0; testDateFrom.isBefore(testDateTo) || testDateFrom.equals(testDateTo); i++) {
            Feed feed = Feed.builder()
                    .owner(users.get(i % 3))
                    .restaurant(restaurants.get(i % 5))
                    // 식당과 서로소가 되도록 1~4를 사용
                    .difficulty(i % 4 + 1)
                    .content("hihihi")
                    .category(CATEGORY_ARR[i % 3])
                    .build();
            feed = feedRepository.save(feed);
            createdDate_f.set(feed, testDateFrom);
            list.add(feedRepository.save(feed));
            testDateFrom = testDateFrom.plusDays(1);
        }
        return list;
    }

}
