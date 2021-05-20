package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    private final UserService userService;
    private final CommentService commentService;

    @Transactional
    public void userLikeComment(Long userId, Long commentId){
        User user = userService.findById(userId);
        Comment comment = commentService.findById(commentId);

        commentLikeRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        commentLike -> {},
                        () -> {
                            commentLikeRepository.save(CommentLike.of(user, comment));
                            commentService.likeCountUp(commentId);
                        }
                );
    }

    @Transactional
    public void userCancelLikeComment(Long userId, Long commentId){
        User user = userService.findById(userId);
        Comment comment = commentService.findById(commentId);

        commentLikeRepository.findByUserAndComment(user, comment)
                .ifPresent(commentLike -> {
                    commentLikeRepository.delete(commentLike);
                    commentService.likeCountDown(commentId);
                });
    }
}
