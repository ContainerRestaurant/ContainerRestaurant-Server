package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedScrapRepository extends JpaRepository<FeedScrap, Long> {

    List<FeedScrap> findAllByUser(User user);

    Optional<FeedScrap> findByUserAndFeed(User user, Feed feed);

}
