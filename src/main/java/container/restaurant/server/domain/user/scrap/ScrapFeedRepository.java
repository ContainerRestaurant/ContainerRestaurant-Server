package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.feed.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ScrapFeedRepository extends JpaRepository<ScrapFeed, Long> {

    @EntityGraph(attributePaths = { "feed", "feed.owner" })
    Page<ScrapFeed> findAllByUserId(Long userId, Pageable pageable);

    Optional<ScrapFeed> findByUserIdAndFeedId(Long userId, Long feedId);

    Boolean existsByUserIdAndFeedId(Long userId, Long feedId);

    void deleteAllByFeed(Feed feed);

    @Query("select sf.user.id from TB_SCRAP_FEED sf where sf.feed.id=:feedId")
    ArrayList<Long> findUserIdByFeedId(Long feedId);

    void deleteAllByUserId(Long userId);

    @Query("select sf.feed.id from TB_SCRAP_FEED sf where sf.user.id=:userId")
    List<Long> findAllByUserId(Long userId);
}
