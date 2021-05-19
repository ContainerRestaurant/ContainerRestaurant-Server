package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.web.dto.statistics.LatestFeedUserInfoDto;
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

    @Transactional
    @Cacheable(cacheNames = "latestFeedCreateUserCache")
    public List<LatestFeedUserInfoDto> latestFeedCreateUsers() {
        return feedService.findByLatestFeedUsers()
                .stream()
                .map(feed -> LatestFeedUserInfoDto.from(feed.getOwner()))
                .collect(Collectors.toList());
    }
}
