package container.restaurant.server.domain.user.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {
}
