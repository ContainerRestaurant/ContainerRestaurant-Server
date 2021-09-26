package container.restaurant.server.domain.user.scrap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScrapFeedRepository extends JpaRepository<ScrapFeed, Long> {

    @EntityGraph(attributePaths = { "feed", "feed.owner" })
    Page<ScrapFeed> findAllByUserId(Long userId, Pageable pageable);

    Optional<ScrapFeed> findByUserIdAndFeedId(Long userId, Long feedId);

    Boolean existsByUserIdAndFeedId(Long userId, Long feedId);

    @Query("select sf.feed.id from TB_SCRAP_FEED sf where sf.user.id=:userId and sf.feed.id in :idList")
    Set<Long> checkScrapFeedOnIdList(Long userId, List<Long> idList);

}
