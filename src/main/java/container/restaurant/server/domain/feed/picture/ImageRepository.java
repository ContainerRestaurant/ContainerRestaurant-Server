package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByFeed(Feed feed);

}
