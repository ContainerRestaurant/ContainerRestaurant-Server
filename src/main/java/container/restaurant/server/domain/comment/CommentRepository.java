package container.restaurant.server.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            "select distinct c from TB_COMMENT c " +
                "join c.feed " +
                "join c.owner " +
                "left join fetch c.replies " +
            "where c.feed.id=:feedId and c.upperReply is null ")
    List<Comment> findFeedComments(Long feedId);

    List<Comment> findAllByUpperReplyId(Long id);

}
