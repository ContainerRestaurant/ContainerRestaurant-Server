package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<Comment, Long> {
}
