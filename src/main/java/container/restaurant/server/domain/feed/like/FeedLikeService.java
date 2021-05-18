package container.restaurant.server.domain.feed.like;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;

    private final UserService userService;
    private final FeedService feedService;

    @Transactional
    public void userLikeFeed(Long userId, Long feedId) {
        User user = userService.findById(userId);
        Feed feed = feedService.findById(feedId);

        feedLikeRepository.findByUserAndFeed(user, feed)
                .ifPresentOrElse(
                        feedLike -> {/* do nothing*/},
                        () -> feedLikeRepository.save(FeedLike.of(user, feed))
                );
    }

    @Transactional
    public void userCancelLikeFeed(Long userId, Long feedId) {
        User user = userService.findById(userId);
        Feed feed = feedService.findById(feedId);

        feedLikeRepository.findByUserAndFeed(user, feed)
                .ifPresent(feedLikeRepository::delete);
    }
}
