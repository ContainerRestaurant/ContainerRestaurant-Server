package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByOwnerId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "owner" })
    Page<Feed> findAllByRestaurantId(Long restaurantId, Pageable pageable);

}
