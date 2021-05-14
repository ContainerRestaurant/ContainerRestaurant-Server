package container.restaurant.server.domain.restaurant.favorite;

import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository extends JpaRepository<RestaurantFavorite, Long> {

    Optional<RestaurantFavorite> findByUserAndRestaurant(User user, Restaurant restaurant);

    List<RestaurantFavorite> findAllByUser(User user);


}
