package container.restaurant.server.domain.feed.like;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.push.event.FeedLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;

    private final UserService userService;
    private final FeedService feedService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public void userLikeFeed(Long userId, Long feedId) {
        User user = userService.findById(userId);
        Feed feed = feedService.findById(feedId);

        feedLikeRepository.findByUserAndFeed(user, feed)
                .orElseGet(() -> {
                    feedLikeRepository.save(FeedLike.of(user, feed));
                    feed.likeCountUp();
                    publisher.publishEvent(new FeedLikedEvent(user, feed));
                    return null;
                });
    }

    @Transactional
    public void userCancelLikeFeed(Long userId, Long feedId) {
        User user = userService.findById(userId);
        Feed feed = feedService.findById(feedId);

        feedLikeRepository.findByUserAndFeed(user, feed)
                .ifPresent(feedLike -> {
                    feedLikeRepository.delete(feedLike);
                    feed.likeCountDown();
                });
    }
}
