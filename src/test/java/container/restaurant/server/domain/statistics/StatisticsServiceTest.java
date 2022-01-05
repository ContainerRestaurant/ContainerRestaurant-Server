package container.restaurant.server.domain.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @InjectMocks
    StatisticsService statisticsService;

    @Test
    @DisplayName("LatestWriter 리스트는 정해진 크기를 넘으면 가장 오래된 값을 제거한다")
    void deleteFirstWhenFull() {
        // Add user under max count
        for (int i = 0; i < StatisticsService.RECENT_USER_MAX_COUNT; i++) {
            User mockUser = createMockUser(i);
            statisticsService.addRecentUser(mockUser);

            long oldestWriterId = statisticsService.getLatestWriters().get(0).getId();

            assertThat(oldestWriterId).isZero();
        }

        // Check eviction when max count exceeded
        int testLoopCount = 5;
        for (int i = 0; i < testLoopCount; i++) {
            User mockUser = createMockUser(i);
            statisticsService.addRecentUser(mockUser);

            List<UserProfileDto> latestWriters = statisticsService.getLatestWriters();

            long expectOldestWriterId = i + 1;
            Long actualOldestWriterId = latestWriters.get(0).getId();

            assertThat(actualOldestWriterId).isEqualTo(expectOldestWriterId);
            assertThat(latestWriters.size()).isEqualTo(StatisticsService.RECENT_USER_MAX_COUNT);
        }
    }

    private User createMockUser(long id) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(id);
        return mockUser;
    }
}
