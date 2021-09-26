package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.BaseDataJpaTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScrapFeedRepositoryTest extends BaseDataJpaTest {

    @Autowired
    ScrapFeedRepository scrapFeedRepository;

    @Test
    @DisplayName("유저의 피드 스크랩 여부 확인 테스트")
    void testExistsByUserAndFeed() {
        //given: user 와 feed 가 주어졌을 때
        User user = newUser();
        Restaurant restaurant = newRestaurant();

        Feed feed = newFeed(user, restaurant);

        //when: 둘 사이에 FeedScrap 을 추가하면
        scrapFeedRepository.save(ScrapFeed.of(user, feed));

        //then-1: user 를 이용해 스크랩을 찾을 수 있다.
        Page<ScrapFeed> scraps = scrapFeedRepository.findAllByUserId(user.getId(), Pageable.unpaged());
        List<ScrapFeed> list = scraps.getContent();
        assertThat(list).isNotNull();
        assertThat(list.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(list.get(0).getFeed().getId()).isEqualTo(feed.getId());

        //then-2: 유저와 피드로 해당 스크랩을 찾을 수 있다.
        ScrapFeed scrap = scrapFeedRepository.findByUserIdAndFeedId(user.getId(), feed.getId())
                .orElse(null);
        assertThat(scrap).isNotNull();
        assertThat(scrap.getUser().getId()).isEqualTo(user.getId());
        assertThat(scrap.getFeed().getId()).isEqualTo(feed.getId());
    }

    @Test
    @DisplayName("주어진 피드 ID 중에 스크랩한 ID 필터링")
    void checkScrapFeedOnIdList() {
        //given 4 개의 피드가 주어졌을 때, 1, 3 번째 피드만 스크랩했을 때
        User user = newUser();
        Restaurant restaurant = newRestaurant();

        Feed feed1 = newFeed(user, restaurant);
        Feed feed2 = newFeed(user, restaurant);
        Feed feed3 = newFeed(user, restaurant);
        Feed feed4 = newFeed(user, restaurant);

        em.persist(ScrapFeed.of(user, feed1));
        em.persist(ScrapFeed.of(user, feed3));

        //when 주어진 피드 ID 리스트를 이용해 주어진 유저가 checkScrapFeedOnIdList() 콜하면
        List<Long> feedIdList = List.of(
                feed1.getId(), feed2.getId(), feed3.getId(), feed4.getId());
        Set<Long> result = scrapFeedRepository.checkScrapFeedOnIdList(user.getId(), feedIdList);

        //then 1, 3 번째 피드 ID 두 개 만을 포함하고 있음
        assertThat(result)
                .hasSize(2)
                .contains(feed1.getId(), feed3.getId())
                .doesNotContain(feed2.getId(), feed4.getId());
    }

}