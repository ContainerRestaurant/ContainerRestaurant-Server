package container.restaurant.server.domain.feed.like;

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
public class FeedLikeService {

    private final UserRepository userRepository;

    private final FeedRepository feedRepository;

    private final FeedLikeRepository feedLikeRepository;

    @Transactional
    public void userLikeFeed(Long userId, Long feedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다.(id:" + userId + ")"));
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 피드입니다.(id:" + feedId + ")"));

        feedLikeRepository.findByUserAndFeed(user, feed)
                .ifPresentOrElse(
                        feedLike -> {/* do nothing*/},
                        () -> feedLikeRepository.save(FeedLike.of(user, feed))
                );
    }

    @Transactional
    public void userCancelLikeFeed(Long userId, Long feedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다.(id:" + userId + ")"));
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 피드입니다.(id:" + feedId + ")"));

        feedLikeRepository.findByUserAndFeed(user, feed)
                .ifPresent(feedLikeRepository::delete);
    }
}
