package container.restaurant.server.domain.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(nativeQuery = true,
            value = "SELECT *, ST_DISTANCE_SPHERE(POINT(?2, ?1), POINT(longitude,latitude)) AS dist\n" +
                    "FROM tb_restaurant FORCE INDEX FOR JOIN (`restaurant-loc-index`)\n" +
                    "WHERE MBRCONTAINS(ST_LINESTRINGFROMTEXT( getDiagonal(?1,?2,?3)), location)")
    List<Restaurant> findNearByRestaurants(double lat, double lon, long radius);

//    식당 이름 검색 비활성화
}