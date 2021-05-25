package container.restaurant.server.domain.feed.hit;

import container.restaurant.server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedHitRepository extends JpaRepository<FeedHit, Long> {

    boolean existsByUserIdAndFeedId(Long userId, Long feedId);
    void deleteAllByFeed(Feed feed);

}
