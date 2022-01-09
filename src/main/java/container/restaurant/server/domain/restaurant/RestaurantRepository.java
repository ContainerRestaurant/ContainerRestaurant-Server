package container.restaurant.server.domain.restaurant;

import container.restaurant.server.domain.restaurant.dto.RestaurantThumbnailDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @NotNull
    @EntityGraph(attributePaths = { "thumbnail" })
    Optional<Restaurant> findById(@NotNull Long id);

    // TODO Thumbnail 과 조인
    @Query(nativeQuery = true,
            value = "SELECT *, ST_DISTANCE_SPHERE(POINT(?2, ?1), POINT(longitude,latitude)) AS dist " +
                    "FROM tb_restaurant FORCE INDEX FOR JOIN (location) " +
                    "WHERE MBRCONTAINS(ST_LINESTRINGFROMTEXT( getDiagonal(?1,?2,?3)), location) " +
                    "ORDER BY dist")
    List<Restaurant> findNearByRestaurants(double lat, double lon, long radius);

    Optional<Restaurant> findByName(String name);

    @Query("select distinct r " +
            "from TB_RESTAURANT r " +
            "join r.menu " +
            "where r.modifiedDate >= :fromDate")
    Page<Restaurant> selectForBestMenuUpdate(LocalDateTime fromDate, Pageable page);

    @Query("select r as restaurant, f.thumbnail as feedThumbnail, max(f.likeCount) as likeCount " +
            "from TB_FEED f " +
            "join f.restaurant r " +
            "where f.modifiedDate >= :fromDate " +
            "   and f.thumbnail is not null " +
            "group by f.restaurant ")
    Page<RestaurantThumbnailDto> selectForRestaurantThumbnailUpdate(LocalDateTime fromDate, Pageable page);
}