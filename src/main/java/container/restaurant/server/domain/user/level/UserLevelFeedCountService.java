package container.restaurant.server.domain.user.level;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserLevelFeedCountService {

    public static final int DAY_LIMIT = 3;

    private final UserLevelFeedCountRepository userLevelRepository;

    @Transactional
    public void levelFeedUp(Feed feed) {
        User owner = feed.getOwner();

        if (updateAndGetResult(owner, feed, 1) <= DAY_LIMIT) {
            owner.levelFeedUp(1);
        }
    }

    @Transactional
    public void levelFeedDown(Feed feed) {
        levelFeedDown(feed, 1);
    }

    @Transactional
    public void levelFeedDown(Feed feed, Integer count) {
        User owner = feed.getOwner();

        if (updateAndGetResult(owner, feed, -count) < DAY_LIMIT) {
            owner.levelFeedDown(count);
        }
    }

    private int updateAndGetResult(User user, Feed feed, Integer count) {
        UserLevelFeedCount levelFeed =
                userLevelRepository.findByUserAndDate(user, feed.getCreatedDate().toLocalDate())
                        .orElseGet(() -> userLevelRepository.save(UserLevelFeedCount.from(feed)));
        return levelFeed.countAggregate(count);
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void deleteExpired() {
        List<UserLevelFeedCount> list = userLevelRepository.findAllByDateBefore(LocalDate.now().minusDays(89));
        list.forEach(levelFeedCount ->
                levelFeedCount.getUser().levelFeedDown(Math.min(DAY_LIMIT, levelFeedCount.getCount())));
        userLevelRepository.deleteAll(list);
    }
    
}
