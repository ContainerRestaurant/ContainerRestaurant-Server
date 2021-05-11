package container.restaurant.server.domain.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(nativeQuery = true,
            value = "SELECT *, ST_DISTANCE_SPHERE(POINT(?2, ?1), POINT(longitude,latitude)) AS dist\n" +
                    "FROM tb_restaurant FORCE INDEX FOR JOIN (`restaurant-loc-index`)\n" +
                    "WHERE MBRCONTAINS(ST_LINESTRINGFROMTEXT( getDiagonal(?1,?2,?3)), location)")
    List<Restaurant> findNearByRestaurants(double lat, double lon, long radius);

    @Query(nativeQuery = true,
            value = "SELECT * FROM tb_restaurant\n" +
                    "WHERE name LIKE CONCAT('%',?1,'%')")
    List<Restaurant> searchRestaurantName(String name);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "UPDATE tb_restaurant set vanish_count = vanish_count +1 where id = ?1")
    int updateVanish(Long id);
}