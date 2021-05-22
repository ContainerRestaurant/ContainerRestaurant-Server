package container.restaurant.server.domain.feed.like;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByUserAndFeed(User user, Feed feed);

    List<FeedLike> findAllByFeed(Feed feed);

    Boolean existsByUserIdAndFeedId(Long userId, Long feedId);

}
