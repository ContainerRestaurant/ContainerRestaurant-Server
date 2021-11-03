package container.restaurant.server.domain.feed.like;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByUserAndFeed(User user, Feed feed);

    List<FeedLike> findAllByFeed(Feed feed);

    Boolean existsByUserIdAndFeedId(Long userId, Long feedId);

    @Query("select fl.feed.id from TB_FEED_LIKE fl where fl.user.id=:userId and fl.feed.id in :idList")
    Set<Long> checkFeedLikeOnIdList(Long userId, List<Long> idList);

    void deleteAllByFeed(Feed feed);

}
