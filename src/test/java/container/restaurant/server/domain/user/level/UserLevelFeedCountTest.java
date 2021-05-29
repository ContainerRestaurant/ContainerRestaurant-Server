package container.restaurant.server.domain.user.level;

import container.restaurant.server.domain.feed.Feed;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserLevelFeedCountTest {

    @ParameterizedTest(name = "카운트 계산 테스트 [{index}] {arguments}")
    @MethodSource
    void countAggregate(int first, int second, int expected) {
        //given 목 피드와 LevelCount 가 주어졌을 때
        Feed feed = mock(Feed.class);
        when(feed.getCreatedDate()).thenReturn(LocalDateTime.now());

        UserLevelFeedCount levelCount = UserLevelFeedCount.from(feed);

        //when first, second 를 aggregate 하면
        int actualFirst = levelCount.countAggregate(first);
        int actualSecond = levelCount.countAggregate(second);

        //then first, expected 가 반환된다.
        assertThat(actualFirst).isEqualTo(first);
        assertThat(actualSecond).isEqualTo(expected);
    }

    static Stream<Arguments> countAggregate() {
        return Stream.of(
                arguments(3, 4, 7),
                arguments(0, 3, 3),
                arguments(4, -3, 1)
        );
    }
}