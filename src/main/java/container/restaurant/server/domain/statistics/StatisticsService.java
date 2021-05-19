package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class StatisticsService {
    private final FeedService feedService;
    private final UserService userService;

    @Transactional
    @Cacheable(cacheNames = "RecentFeedUsersCache")
    public List<StatisticsUserInfoDto> getRecentFeedUsers() {
        return feedService.findByLatestFeeds()
                .stream()
                .map(feed -> StatisticsUserInfoDto.from(feed.getOwner()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Cacheable(cacheNames = "MostFeedUsersCache")
    public List<StatisticsUserInfoDto> getMostFeedUsers() {
        return userService.findByFeedCountTopUser()
                .stream()
                .map(StatisticsUserInfoDto::from)
                .collect(Collectors.toList());
    }
}
