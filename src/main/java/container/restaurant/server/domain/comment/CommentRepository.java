package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeed(Feed feed);
}
