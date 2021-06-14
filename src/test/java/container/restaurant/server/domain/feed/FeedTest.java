package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FeedTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        User user = User.builder()
                .email("test@test.com")
                .profile(new Image("testImage"))
                .build();
        String description = "description";
        Boolean welcome = true;
        Integer difficulty = 5;

        //when
        Feed feed = Feed.builder()
                .owner(user)
                .content(description)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();

        //then
        assertThat(feed.getId()).isNull();
        assertThat(feed.getOwner()).isEqualTo(user);
        assertThat(feed.getContent()).isEqualTo(description);
        assertThat(feed.getWelcome()).isEqualTo(welcome);
        assertThat(feed.getDifficulty()).isEqualTo(difficulty);
        assertThat(feed.getLikeCount()).isEqualTo(0);
        assertThat(feed.getScrapCount()).isEqualTo(0);
        assertThat(feed.getReplyCount()).isEqualTo(0);
        assertThat(feed.getIsBlind()).isFalse();
        assertThat(feed.getIsDeleted()).isFalse();
        assertThat(feed.getCreatedDate()).isNull();
        assertThat(feed.getModifiedDate()).isNull();
    }

    @ParameterizedTest(name = "[{index}]")
    @MethodSource
    void testComparator(Feed feed1, Feed feed2, int expect) {
        int res = Feed.RECOMMEND_COMPARATOR.compare(feed1, feed2);

        if (expect == 0) {
            assertThat(res).isEqualTo(0);
        } else {
            assertThat(res * expect).isGreaterThan(0);
        }
    }

    static Stream<Arguments> testComparator() throws Exception {
        return Stream.of(
                arguments(
                        recommendTestFeed(5, 0, LocalDate.now()),
                        recommendTestFeed(1, 20, LocalDate.now()),
                        0
                ),
                arguments(
                        recommendTestFeed(6, 0, LocalDate.now()),
                        recommendTestFeed(1, 20, LocalDate.now()),
                        1
                ),
                arguments(
                        recommendTestFeed(5, 0, LocalDate.now().minusDays(1)),
                        recommendTestFeed(1, 20, LocalDate.now()),
                        -1
                )
        );
    }

    static Feed recommendTestFeed(Integer likeCount, Integer hitCount, LocalDate createDate
    ) throws Exception {
        Feed feed = new Feed();

        Field field = feed.getClass().getDeclaredField("likeCount");
        field.setAccessible(true);
        field.set(feed, likeCount);

        field = feed.getClass().getDeclaredField("hitCount");
        field.setAccessible(true);
        field.set(feed, hitCount);

        field = feed.getClass().getSuperclass().getSuperclass().getDeclaredField("createdDate");
        field.setAccessible(true);
        field.set(feed, LocalDateTime.of(createDate, LocalTime.MIN));

        return feed;
    }

}