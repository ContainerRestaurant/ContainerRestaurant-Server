package container.restaurant.server.domain.user.scrap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapFeedRepository extends JpaRepository<ScrapFeed, Long> {

    @EntityGraph(attributePaths = { "feed", "feed.owner" })
    Page<ScrapFeed> findAllByUserId(Long userId, Pageable pageable);

    Optional<ScrapFeed> findByUserIdAndFeedId(Long userId, Long feedId);

}
