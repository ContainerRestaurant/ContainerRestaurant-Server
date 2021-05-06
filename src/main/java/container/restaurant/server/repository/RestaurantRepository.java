package container.restaurant.server.repository;


import container.restaurant.server.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(nativeQuery = true,
            value = "SELECT *, ST_DISTANCE_SPHERE(POINT(?2, ?1), POINT(longitude,latitude)) AS dist\n" +
                    "FROM TB_RESTAURANT FORCE INDEX FOR JOIN (`restaurant-loc-index`)\n" +
                    "WHERE MBRCONTAINS(ST_LINESTRINGFROMTEXT( getDiagonal(?2,?1,?3)), location)")

    List<Restaurant> findNearByRestaurants(float lat, float lon, long radius);

}
