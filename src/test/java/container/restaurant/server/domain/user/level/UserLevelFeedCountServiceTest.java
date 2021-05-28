package container.restaurant.server.domain.user.level;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserLevelFeedCountServiceTest extends BaseServiceTest {

    @InjectMocks
    UserLevelFeedCountService userLevelFeedCountService;

    @Mock
    User user;

    @Mock
    Feed feed;

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3, 4, 5})
    void levelFeedUp(int initCount) {
        //given 영속을 mock 한 feed 와 피드 작성자의 UserLevelFeedCount 가 주어졌을 떄
        when(feed.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(feed.getOwner()).thenReturn(user);

        UserLevelFeedCount spy = UserLevelFeedCount.from(feed);
        spy.countAggregate(initCount);
        spy = spy(spy);
        when(userLevelFeedCountRepository.findByUserAndDate(any(), any()))
                .thenReturn(Optional.of(spy));

        //when 함수를 호출하면
        userLevelFeedCountService.levelFeedUp(feed);

        //then UserLevelFeedCount 가 1 만큼 aggregate 되고, 횟수가 하루 제한 이하이면 levelFeedUp() 한다.
        verify(spy).countAggregate(1);
        if (spy.getCount() <= UserLevelFeedCountService.DAY_LIMIT) {
            verify(user).levelFeedUp(1);
        } else {
            verify(user, never()).levelFeedUp(1);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"0,0", "3,2", "5,2", "6,4", "2,1"})
    void levelFeedDown(int initCount, int count) {
        //given 영속을 mock 한 feed 와 피드 작성자의 UserLevelFeedCount 가 주어졌을 떄
        when(feed.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(feed.getOwner()).thenReturn(user);

        UserLevelFeedCount spy = UserLevelFeedCount.from(feed);
        spy.countAggregate(initCount);
        spy = spy(spy);
        when(userLevelFeedCountRepository.findByUserAndDate(any(), any()))
                .thenReturn(Optional.of(spy));

        //when 함수를 호출하면
        userLevelFeedCountService.levelFeedDown(feed, count);

        //then UserLevelFeedCount 가 -count 만큼 aggregate 되고, 횟수가 하루 제한 미만이면 levelFeedDown() 한다.
        verify(spy).countAggregate(-count);
        if (spy.getCount() < UserLevelFeedCountService.DAY_LIMIT) {
            verify(user).levelFeedDown(count);
        } else {
            verify(user, never()).levelFeedDown(count);
        }
    }

}