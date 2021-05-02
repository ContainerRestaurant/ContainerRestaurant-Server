package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FeedScrapRepositoryTest {

    @Autowired
    FeedScrapRepository feedScrapRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void afterEach() {
        feedScrapRepository.deleteAll();
        feedRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testExistsByUserAndFeed() {
        //given: user 와 feed 가 주어졌을 때
        User user = userRepository.save(User.builder()
                .email("test@test.com")
                .profile("https://my.profile")
                .build());

        Feed feed = feedRepository.save(Feed.builder()
                .owner(user)
                .difficulty(3)
                .build());

        //when: 둘 사이에 FeedScrap 을 추가하면
        feedScrapRepository.save(FeedScrap.of(user, feed));

        //then-1: user 를 이용해 스크랩을 찾을 수 있다.
        List<FeedScrap> userScraps  = feedScrapRepository.findAllByUser(user);
        assertThat(userScraps).isNotNull();
        assertThat(userScraps.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(userScraps.get(0).getFeed().getId()).isEqualTo(feed.getId());

        //then-2: 유저와 피드로 해당 스크랩을 찾을 수 있다.
        FeedScrap scrap = feedScrapRepository.findByUserAndFeed(user, feed)
                .orElse(null);
        assertThat(scrap).isNotNull();
        assertThat(scrap.getUser().getId()).isEqualTo(user.getId());
        assertThat(scrap.getFeed().getId()).isEqualTo(feed.getId());
    }

}