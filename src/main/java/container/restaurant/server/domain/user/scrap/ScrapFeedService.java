package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScrapFeedService {

    private final ScrapFeedRepository scrapFeedRepository;

    private final UserService userService;
    private final FeedService feedService;

    @Transactional
    public void userScrapFeed(Long userId, Long feedId) {
        Feed feed = feedService.findById(feedId);
        User user = userService.findById(userId);

        scrapFeedRepository.findByUserIdAndFeedId(userId, feedId)
                .ifPresentOrElse(
                        scrap -> {/* do nothing */},
                        () -> {
                            scrapFeedRepository.save(ScrapFeed.of(user, feed));
                            user.scrapCountUp();
                        });
    }

    @Transactional
    public void userCancelScrapFeed(Long userId, Long feedId) {
        User user = userService.findById(userId);

        scrapFeedRepository.findByUserIdAndFeedId(userId, feedId)
                .ifPresent(scrap -> {
                    scrapFeedRepository.delete(scrap);
                    user.scrapCountDown();
                });
    }
}
