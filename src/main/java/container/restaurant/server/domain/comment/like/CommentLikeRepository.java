package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
