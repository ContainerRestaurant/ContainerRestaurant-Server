package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.recommend.RecommendFeedService;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsDto;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
    public static final int RECENT_USER_MAX_COUNT = 100;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final RecommendFeedService recommendFeedService;

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final AtomicInteger todayFeedCount = new AtomicInteger(0);

    private final LatestWriterList<UserProfileDto> latestWriters = new LatestWriterList<>(RECENT_USER_MAX_COUNT);
    private List<UserProfileDto> topWriters = new CopyOnWriteArrayList<>();

    private final AtomicLong feedCountUntilUpdate = new AtomicLong(0);
    private final AtomicLong feedWriterCountUntilUpdate = new AtomicLong(0);

    @Override
    public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
        init();
    }

    public void init() {
        updateLatestWriters();
        updateTopWriters();
        updateCounts();
        recommendFeedService.updateRecommendFeed();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyUpdate() {
        updateTopWriters();
        updateCounts();
        updateBestMenus();
        updateRestaurantThumbnail();
        recommendFeedService.updateRecommendFeed();
    }

    private void updateLatestWriters() {
        latestWriters.replaceAll(userRepository.findLatestUsers(PageRequest.of(0, 100)));
    }

    private void updateTopWriters() {
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime to = LocalDateTime.of(from.minusMonths(1).toLocalDate(), LocalTime.MIN);

        // 현재 최다 피드 사용자는 탑 10 명으로 순서는 따로 매기지 않는다.
        topWriters = userService.findByFeedCountTopUsers(to, from).stream()
                        .map(UserProfileDto::from)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    private void updateCounts() {
        this.feedCountUntilUpdate.set(feedRepository.count());
        this.feedWriterCountUntilUpdate.set(userRepository.writerCount());
        this.todayFeedCount.set(0);
    }

    public void addRecentUser(User user) {
        UserProfileDto dto = UserProfileDto.from(user);

        latestWriters.add(dto);
        todayFeedCount.incrementAndGet();
    }

    public void removeRecentUser(User user) {
        latestWriters.remove(UserProfileDto.from(user));
        todayFeedCount.decrementAndGet();
    }

    public Long getTotalFeed() {
        return this.feedCountUntilUpdate.get() + todayFeedCount.get();
    }

    public List<UserProfileDto> getLatestWriters() {
        return this.latestWriters.getList();
    }

    public StatisticsDto.TotalContainer totalContainer() {
        return StatisticsDto.TotalContainer.builder()
                .feedCount(feedCountUntilUpdate.get())
                .writerCount(feedWriterCountUntilUpdate.get())
                .latestWriters(latestWriters.getList())
                .topWriters(topWriters)
                .build();
    }

    private void updateBestMenus() {
        Pageable page = PageRequest.of(0, 1000);

        while (!Objects.equals(page, unpaged())) {
            page = restaurantService.updateBestMenusPage(page);
        }
    }

    private void updateRestaurantThumbnail() {
        Pageable page = PageRequest.of(0, 1000);

        while (!Objects.equals(page, unpaged())) {
            page = restaurantService.updateThumbnailPage(page);
        }
    }

}
