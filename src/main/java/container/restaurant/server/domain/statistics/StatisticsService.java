package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsDto;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class StatisticsService implements ApplicationRunner {
    private static final int MAX_COUNT = 100;
    private final UserService userService;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private int todayFeedCount = 0;

    private Deque<UserProfileDto> latestWriters = new LinkedList<>();
    private List<UserProfileDto> topWriters = new ArrayList<>();

    private long feedCountUntilUpdate;


    @Override
    public void run(ApplicationArguments args) {
        updateWritersStatistic();
    }

    public void updateWritersStatistic() {
        updateLatestWriters();
        updateTopWriters();
        refreshTotalFeed();
    }

    public void updateLatestWriters() {
        latestWriters = new LinkedList<>(userRepository.findLatestUsers(PageRequest.of(0, 100)));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateTopWriters() {
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime to = LocalDateTime.of(from.minusMonths(1).toLocalDate(), LocalTime.MIN);

        // 현재 최다 피드 사용자는 탑 10 명으로 순서는 따로 매기지 않는다.
        topWriters = userService.findByFeedCountTopUsers(to, from).stream()
                        .map(UserProfileDto::from)
                        .collect(Collectors.toList());
    }

    private void refreshTotalFeed() {
        this.feedCountUntilUpdate = feedRepository.count();
        this.todayFeedCount = 0;
    }

    public void addRecentUser(User user) {
        UserProfileDto dto = UserProfileDto.from(user);
        if (!latestWriters.remove(dto) && latestWriters.size() > MAX_COUNT) {
            latestWriters.removeFirst();
        }

        latestWriters.add(dto);
        todayFeedCount++;
    }

    public void removeRecentUser(User user) {
        todayFeedCount--;
        latestWriters.remove(UserProfileDto.from(user));
    }

    public Long getTotalFeed() {
        return this.feedCountUntilUpdate + todayFeedCount;
    }

    public Deque<UserProfileDto> getLatestWriters() {
        return this.latestWriters;
    }

    public StatisticsDto.TotalContainer totalContainer() {
        return StatisticsDto.TotalContainer.builder()
                .feedCount(feedCountUntilUpdate)
                .writerCount(0L)
                .latestWriters(latestWriters)
                .topWriters(topWriters)
                .build();
    }

}
