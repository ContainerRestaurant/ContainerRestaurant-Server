package container.restaurant.server.domain.restaurant;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @NotNull
    @EntityGraph(attributePaths = { "thumbnail" })
    Optional<Restaurant> findById(@NotNull Long id);

    // TODO Thumbnail 과 조인
    @Query(nativeQuery = true,
            value = "SELECT *, ST_DISTANCE_SPHERE(POINT(?2, ?1), POINT(longitude,latitude)) AS dist " +
                    "FROM tb_restaurant FORCE INDEX FOR JOIN (`restaurant-loc-index`) " +
                    "WHERE MBRCONTAINS(ST_LINESTRINGFROMTEXT( getDiagonal(?1,?2,?3)), location) " +
                    "ORDER BY dist")
    List<Restaurant> findNearByRestaurants(double lat, double lon, long radius);

    Optional<Restaurant> findByName(String name);
}