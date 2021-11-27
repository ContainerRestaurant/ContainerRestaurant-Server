package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsDto;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Pageable.unpaged;

@RequiredArgsConstructor
@Service
@Log4j2
public class StatisticsService implements ApplicationListener<ApplicationStartedEvent> {
    private static final int MAX_COUNT = 100;
    private final UserService userService;
    private final RestaurantService restaurantService;

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private int todayFeedCount = 0;

    private Deque<UserProfileDto> latestWriters = new LinkedList<>();
    private List<UserProfileDto> topWriters = new ArrayList<>();

    private long feedCountUntilUpdate;
    private long feedWriterCountUntilUpdate;

    @Override
    public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
        init();
    }

    public void init() {
        updateLatestWriters();
        updateTopWriters();
        updateCounts();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyUpdate() {
        updateTopWriters();
        updateCounts();
        updateBestMenus();
    }

    private void updateLatestWriters() {
        latestWriters = new LinkedList<>(userRepository.findLatestUsers(PageRequest.of(0, 100)));
    }

    private void updateTopWriters() {
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime to = LocalDateTime.of(from.minusMonths(1).toLocalDate(), LocalTime.MIN);

        // 현재 최다 피드 사용자는 탑 10 명으로 순서는 따로 매기지 않는다.
        topWriters = userService.findByFeedCountTopUsers(to, from).stream()
                        .map(UserProfileDto::from)
                        .collect(Collectors.toList());
    }

    private void updateCounts() {
        this.feedCountUntilUpdate = feedRepository.count();
        this.feedWriterCountUntilUpdate = userRepository.writerCount();
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
                .writerCount(feedWriterCountUntilUpdate)
                .latestWriters(latestWriters)
                .topWriters(topWriters)
                .build();
    }

    private void updateBestMenus() {
        Pageable page = PageRequest.of(1, 1000);

        while (!Objects.equals(page, unpaged())) {
            page = restaurantService.updateBestMenusPage(page);
        }
    }

}
