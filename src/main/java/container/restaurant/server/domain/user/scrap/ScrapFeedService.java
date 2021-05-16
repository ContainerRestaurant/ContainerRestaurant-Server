package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScrapFeedService {

    private final UserRepository userRepository;

    private final FeedRepository feedRepository;

    private final ScrapFeedRepository scrapFeedRepository;

    @Transactional
    public void userScrapFeed(Long userId, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 피드입니다.(id:" + feedId + ")"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다.(id:" + userId + ")"));

        scrapFeedRepository.findByUserIdAndFeedId(userId, feedId)
                .ifPresentOrElse(
                        scrap -> {/* do nothing */},
                        () -> {
                            scrapFeedRepository.save(ScrapFeed.of(user, feed));
                            userRepository.save(user.scrapCountUp());
                        });
    }

    @Transactional
    public void userCancelScrapFeed(Long userId, Long feedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다.(id:" + userId + ")"));

        scrapFeedRepository.findByUserIdAndFeedId(userId, feedId)
                .ifPresent(scrap -> {
                    scrapFeedRepository.delete(scrap);
                    userRepository.save(user.scrapCountDown());
                });
    }
}
