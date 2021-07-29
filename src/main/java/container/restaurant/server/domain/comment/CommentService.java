package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.push.event.FeedCommentedEvent;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private final UserService userService;
    private final FeedService feedService;

    private final ApplicationEventPublisher publisher;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public Long createComment(CommentCreateDto commentCreateDto, Long feedId, Long userId) {
        Feed feed = feedService.findById(feedId);
        User user = userService.findById(userId);

        Comment comment = commentRepository.save(commentCreateDto.toEntityWith(user, feed));
        ofNullable(commentCreateDto.getUpperReplyId())
                .ifPresent(upperReplyId -> comment.isBelongTo(findById(upperReplyId)));

        feed.commentCountUp();
        publisher.publishEvent(new FeedCommentedEvent(comment));

        return comment.getId();
    }

    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:" + id + ")"));
    }

    @Transactional(readOnly = true)
    public CommentInfoDto get(Long id) {
        return CommentInfoDto.from(findById(id));
    }

    @Transactional(readOnly = true)
    public CollectionModel<CommentInfoDto> findAllByFeed(Long userId, Long feedId) throws ResourceNotFoundException {
        List<Comment> commentList = commentRepository.findFeedComments(feedId);
        Set<Long> likeIds = commentLikeRepository.findCommentIdsByFeedIdAndUserId(userId, feedId);

        List<CommentInfoDto> result = commentList.stream()
                .map(c -> CommentInfoDto.from(c, likeIds))
                .collect(Collectors.toList());

        return CollectionModel.of(result);
    }

    @Transactional
    public void deleteById(Long id, Long userId) {
        Comment comment = findById(id);

        if (!comment.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("삭제 할 수 있는 유저가 아닙니다.");

        if (comment.hasReply()) {
            comment.delete();
        } else {
            ofNullable(comment.getUpperReply())
                    .filter(upperComment -> upperComment.getReplies().size() == 1)
                    .ifPresent(upperComment -> {
                        if (upperComment.isDeleted())
                            commentRepository.delete(upperComment);
                        else
                            upperComment.removeReply(comment);
                    });
            commentRepository.delete(comment);
        }
        comment.getFeed().commentCountDown();
    }

    @Transactional
    public void update(Long commentId, CommentUpdateDto dto, Long userId) {
        Comment comment = findById(commentId);

        if (!comment.getOwner().getId().equals(userId))
            throw new FailedAuthorizationException("수정 할 수 있는 유저가 아닙니다.");

        dto.updateComment(comment);
    }

    @Transactional
    public void likeCountUp(Long commentId) {
        findById(commentId).likeCountUp();
    }

    @Transactional
    public void likeCountDown(Long commentId) {
        findById(commentId).likeCountDown();
    }
}
