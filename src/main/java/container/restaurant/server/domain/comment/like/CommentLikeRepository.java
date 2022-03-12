package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);

    @Query("select c.id " +
            "from TB_COMMENT_LIKE cl " +
                "left join cl.user u " +
                "left join cl.comment c " +
            "where u.id = ?1 and c in ?2")
    Set<Long> test(Long userId, Collection<Comment> comments);

    @Modifying
    @Transactional
    @Query("delete from TB_COMMENT_LIKE cl where cl.comment.id in :commentIdList")
    void deleteAllByCommentId(List<Long> commentIdList);

    void deleteAllByUserId(Long userId);

    @Query("select cl.comment.id from TB_COMMENT_LIKE cl where cl.user.id=:userId")
    List<Long> findAllByUserId(Long userId);

}
