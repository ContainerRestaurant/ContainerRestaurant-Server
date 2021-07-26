package container.restaurant.server.web.base;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFeedAndCommentControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected CommentLikeRepository commentLikeRepository;

    protected Comment myFeedComment;
    protected Comment myFeedCommentReply;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        myFeedComment = commentRepository.save(Comment.builder()
                .owner(other)
                .feed(myFeed)
                .content("내 피드에 대댓글이 있는 댓글")
                .build());

        myFeedCommentReply = Comment.builder()
                .owner(myself)
                .feed(myFeed)
                .content("내 피드 댓글의 대댓글")
                .build();
        myFeedCommentReply.isBelongTo(myFeedComment);
        commentRepository.save(myFeedCommentReply);
    }

    @Override
    @AfterEach
    public void afterEach() {
        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        super.afterEach();
    }

}
