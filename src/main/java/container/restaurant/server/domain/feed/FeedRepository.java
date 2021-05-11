package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByOwner(User owner, Pageable pageable);

    Page<Feed> findAllByRestaurant(Restaurant restaurant, Pageable pageable);

}
