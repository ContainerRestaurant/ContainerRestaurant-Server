package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapFeedRepository extends JpaRepository<ScrapFeed, Long> {

    List<ScrapFeed> findAllByUser(User user);

    Optional<ScrapFeed> findByUserAndFeed(User user, Feed feed);

}
