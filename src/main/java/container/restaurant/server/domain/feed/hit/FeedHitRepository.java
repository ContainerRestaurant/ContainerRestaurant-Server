package container.restaurant.server.domain.feed.hit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedHitRepository extends JpaRepository<FeedHit, Long> {

    boolean existsByUserIdAndFeedId(Long userId, Long feedId);

}
