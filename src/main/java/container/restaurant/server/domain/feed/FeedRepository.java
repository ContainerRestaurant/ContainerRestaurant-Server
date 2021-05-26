package container.restaurant.server.domain.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"owner", "restaurant", "containerList", "containerList.menu"})
    Optional<Feed> findById(@NonNull Long id);

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByOwnerId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByRestaurantId(Long restaurantId, Pageable pageable);

    @Query("select f from TB_FEED f join f.scrapedBy s where s.user.id = ?1")
    Page<Feed> findAllByScraperId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByCategory(Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByOwnerIdAndCategory(Long userId, Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByRestaurantIdAndCategory(Long restaurantId, Pageable pageable, Category category);

    @Query("select f from TB_FEED f join f.scrapedBy s where s.user.id = ?1 and f.category = ?2")
    Page<Feed> findAllByScraperIdAndCategory(Long userId, Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);

}
