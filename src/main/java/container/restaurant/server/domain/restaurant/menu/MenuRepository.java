package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByRestaurantAndName(Restaurant restaurant, String name);

}
