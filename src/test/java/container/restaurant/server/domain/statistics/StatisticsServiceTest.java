package container.restaurant.server.domain.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsDto.TotalContainer;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import container.restaurant.server.web.dto.user.UserDto.Info;
import container.restaurant.server.web.dto.user.UserDto.Update;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class StatisticsServiceTest {

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("유저 정보가 수정되면 통계에도 반영된다")
    void changeUserInfoInStatisticsWhenUserUpdate() {
        final long userIdForUpdate = 1L;
        final String beforeUpdateNickname = "nickname";
        final String afterUpdateNickname = "changed-nickname";

        final User userForUpdate = User.builder()
                .nickname(beforeUpdateNickname)
                .build();

        // Add user to statistics
        statisticsService.afterFeedCreate(userForUpdate);

        Collection<UserProfileDto> latestWriters = statisticsService.totalContainer().getLatestWriters();
        assertThat(containsUserWithNickname(latestWriters, beforeUpdateNickname)).isTrue();
        assertThat(containsUserWithNickname(latestWriters, afterUpdateNickname)).isFalse();

        // Update user
        when(userRepository.findById(userIdForUpdate)).thenReturn(Optional.of(userForUpdate));
        Info update = userService.update(userIdForUpdate, Update.builder().nickname(afterUpdateNickname).build());

        assertThat(update.getNickname()).isNotEqualTo(beforeUpdateNickname);
        assertThat(update.getNickname()).isEqualTo(afterUpdateNickname);

        // Check user info in statistics
        latestWriters = statisticsService.totalContainer().getLatestWriters();

        assertThat(containsUserWithNickname(latestWriters, beforeUpdateNickname)).isFalse();
        assertThat(containsUserWithNickname(latestWriters, afterUpdateNickname)).isTrue();

    }

    @Test
    @DisplayName("LatestWriter 리스트는 정해진 크기를 넘으면 가장 오래된 값을 제거한다")
    void deleteFirstWhenFull() {
        // Add user under max count
        for (int i = 0; i < StatisticsService.RECENT_WRITER_MAX_COUNT; i++) {
            User mockUser = createMockUser(i);
            statisticsService.afterFeedCreate(mockUser);

            long oldestWriterId = statisticsService.getLatestWriters().get(0).getId();

            assertThat(oldestWriterId).isZero();
        }

        // Check eviction when max count exceeded
        int testLoopCount = 5;
        for (int i = 0; i < testLoopCount; i++) {
            User mockUser = createMockUser(i);
            statisticsService.afterFeedCreate(mockUser);

            List<UserProfileDto> latestWriters = statisticsService.getLatestWriters();

            long expectOldestWriterId = i + 1;
            Long actualOldestWriterId = latestWriters.get(0).getId();

            assertThat(actualOldestWriterId).isEqualTo(expectOldestWriterId);
            assertThat(latestWriters.size()).isEqualTo(StatisticsService.RECENT_WRITER_MAX_COUNT);
        }
    }

    @Test
    @DisplayName("총 피드 수, 총 피드 작성자 수 테스트")
    void updateFeedCountRealtime() {
        TotalContainer totalContainer = statisticsService.totalContainer();

        assertThat(totalContainer.getWriterCount()).isZero();
        assertThat(totalContainer.getFeedCount()).isZero();

        User mockUser1 = createMockUser(1L);
        User mockUser2 = createMockUser(2L);

        int mockUser1FeedCount = 0;
        int mockUser2FeedCount = 0;

        // user1 피드 1개 작성, user2 피드 2개 작성
        when(mockUser1.getFeedCount()).thenReturn(++mockUser1FeedCount);
        statisticsService.afterFeedCreate(mockUser1);
        when(mockUser2.getFeedCount()).thenReturn(++mockUser2FeedCount);
        statisticsService.afterFeedCreate(mockUser2);
        when(mockUser2.getFeedCount()).thenReturn(++mockUser2FeedCount);
        statisticsService.afterFeedCreate(mockUser2);

        totalContainer = statisticsService.totalContainer();

        assertThat(totalContainer.getWriterCount()).isEqualTo(2);
        assertThat(totalContainer.getFeedCount()).isEqualTo(3);

        // user2 자신 피드 2개 중 1개 삭제
        when(mockUser2.getFeedCount()).thenReturn(--mockUser2FeedCount);
        statisticsService.afterFeedDelete(mockUser2);

        totalContainer = statisticsService.totalContainer();

        assertThat(totalContainer.getWriterCount()).isEqualTo(2);
        assertThat(totalContainer.getFeedCount()).isEqualTo(2);

        // user2 하나 남은 피드 삭제
        when(mockUser2.getFeedCount()).thenReturn(--mockUser2FeedCount);
        statisticsService.afterFeedDelete(mockUser2);

        totalContainer = statisticsService.totalContainer();

        assertThat(totalContainer.getWriterCount()).isEqualTo(1);
        assertThat(totalContainer.getFeedCount()).isEqualTo(1);

        // user1 하나 남은 피드 삭제
        when(mockUser1.getFeedCount()).thenReturn(--mockUser1FeedCount);
        statisticsService.afterFeedDelete(mockUser1);

        totalContainer = statisticsService.totalContainer();

        assertThat(totalContainer.getWriterCount()).isZero();
        assertThat(totalContainer.getFeedCount()).isZero();
    }

    private boolean containsUserWithNickname(Collection<UserProfileDto> users, String nickName) {
        for (UserProfileDto user : users) {
            if (user.getNickname().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    private User createMockUser(long id) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(id);
        return mockUser;
    }
}
