package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.push.event.CommentLikedEvent;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    private final UserService userService;
    private final CommentService commentService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public void userLikeComment(Long userId, Long commentId) {
        User user = userService.findById(userId);
        Comment comment = commentService.findById(commentId);

        commentLikeRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        commentLike -> {
                        },
                        () -> {
                            commentLikeRepository.save(CommentLike.of(user, comment));
                            commentService.likeCountUp(commentId);
                            publisher.publishEvent(new CommentLikedEvent(user, comment));
                        }
                );
    }

    @Transactional
    public void userCancelLikeComment(Long userId, Long commentId) {
        User user = userService.findById(userId);
        Comment comment = commentService.findById(commentId);

        commentLikeRepository.findByUserAndComment(user, comment)
                .ifPresent(commentLike -> {
                    commentLikeRepository.delete(commentLike);
                    commentService.likeCountDown(commentId);
                });
    }
    @Transactional
    public void deleteAllByUserId(Long userId){
        List<Long> commentIdList=commentLikeRepository.findAllByUserId(userId);
        for(Long commentId:commentIdList){
            commentService.likeCountDown(commentId);
        }
        commentLikeRepository.deleteAllByUserId(userId);
    }
}
