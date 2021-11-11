package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeed(Feed feed);

    @Query("select c.id from TB_COMMENT c where c.feed.id=:feedId")
    List<Long> findCommentIdByFeedId(Long feedId);

    List<Comment> findAllByUpperReplyId(Long id);
    void deleteAllByFeed(Feed feed);
}
