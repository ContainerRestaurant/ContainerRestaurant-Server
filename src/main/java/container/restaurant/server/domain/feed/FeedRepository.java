package container.restaurant.server.domain.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "owner.profile", "thumbnail", "restaurant", "containerList", "containerList.menu" })
    Optional<Feed> findById(@NonNull Long id);

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "owner", "thumbnail","restaurant" })
    Page<Feed> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "owner", "thumbnail" })
    Page<Feed> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("select f.id from TB_FEED f where f.owner.id=:ownerId")
    List<Long> findAllByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = { "owner", "thumbnail" })
    Page<Feed> findAllByRestaurantId(Long restaurantId, Pageable pageable);

    @Query("select f from TB_FEED f join f.scrapedBy s join f.owner left join f.thumbnail where s.user.id = ?1")
    Page<Feed> findAllByScraperId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "owner", "thumbnail" })
    Page<Feed> findAllByCategory(Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner", "thumbnail" })
    Page<Feed> findAllByOwnerIdAndCategory(Long userId, Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner", "thumbnail" })
    Page<Feed> findAllByRestaurantIdAndCategory(Long restaurantId, Pageable pageable, Category category);

    @Query("select f from TB_FEED f join f.scrapedBy s join f.owner left join f.thumbnail where s.user.id = ?1 and f.category = ?2")
    Page<Feed> findAllByScraperIdAndCategory(Long userId, Pageable pageable, Category category);

    @EntityGraph(attributePaths = { "owner", "thumbnail", "restaurant" })
    Page<Feed> findAllByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("select f " +
            "from TB_FEED f " +
            "where f.modifiedDate <= :fromDate " +
            "   and f.thumbnail is not null " +
            "group by f.restaurant " +
            "having max(f.likeCount)")
    Page<Feed> selectForRestaurantThumbnailUpdate(LocalDate fromDate, Pageable page);
}
