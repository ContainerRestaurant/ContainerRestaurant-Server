package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.push.event.FeedCommentedEvent;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private final UserService userService;
    private final FeedService feedService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public CommentInfoDto createComment(CommentCreateDto commentCreateDto, Long feedId, Long userId) {
        Feed feed = feedService.findById(feedId);
        User user = userService.findById(userId);

        Comment comment;
        if (commentCreateDto.getUpperReplyId() == null) {
            comment = new Comment(user, feed, commentCreateDto.getContent());
        } else {
            Comment upperReply = findById(commentCreateDto.getUpperReplyId());
            upperReply.setIsHaveReply();
            comment = new Comment(commentCreateDto, feed, user, upperReply);
        }
        feed.commentCountUp();

        publisher.publishEvent(new FeedCommentedEvent(comment));
        return CommentInfoDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:" + id + ")"));
    }

    @Transactional(readOnly = true)
    public CollectionModel<CommentInfoDto> findAllByFeed(Long feedId) throws ResourceNotFoundException {
        Feed feed = feedService.findById(feedId);

        LinkedHashMap<Long, CommentInfoDto> commentDtoMap = new LinkedHashMap<>();

        commentRepository.findAllByFeed(feed).forEach(comment ->
                ofNullable(comment.getUpperReply()).ifPresentOrElse(
                        upperReply -> commentDtoMap.get(upperReply.getId())
                                .addCommentReply(CommentInfoDto.from(comment)),
                        () -> commentDtoMap.put(comment.getId(), CommentInfoDto.from(comment))
                ));

        return CollectionModel.of(commentDtoMap.values());
    }

    @Transactional
    public void deleteById(Long id, Long userId) {
        Comment comment = findById(id);

        if (!comment.getOwner().getId().equals(userId))
            throw new ResourceNotFoundException("삭제 할 수 있는 유저가 아닙니다.");

        // 대댓글 있다면(isHaveReply) 댓글 isDeleted 처리
        if (comment.getIsHaveReply()) {
            comment.setIsDeleted();
            return;
        }
        Comment upperReply = comment.getUpperReply();
        // 만약 답글이 삭제 되는 것이라면 상위 댓글의 IsHaveReply = false 처리
        List<Comment> UpperReplies = commentRepository.findCommentsByUpperReplyId(upperReply.getId());
        if (UpperReplies.size() == 1) {
            upperReply.unSetIsHaveReply();
            // 답글 삭제 시 상위 댓글의 isDeleted가 true라면 상위댓글도 삭제
            if (upperReply.getIsDeleted())
                commentRepository.deleteById(upperReply.getId());
        }
        // delete
        commentRepository.deleteById(id);
        comment.getFeed().commentCountDown();
    }

    @Transactional
    public CommentInfoDto update(Long commentId, CommentUpdateDto commentUpdateDto, Long userId) {
        Comment comment = findById(commentId);

        if (!comment.getOwner().getId().equals(userId))
            throw new ResourceNotFoundException("수정 할 수 있는 유저가 아닙니다.");

        return CommentInfoDto.from(comment.setContent(commentUpdateDto.getContent()));
    }

    @Transactional
    public void likeCountUp(Long commentId) {
        Comment comment = findById(commentId);
        comment.likeCountUp();
    }

    @Transactional
    public void likeCountDown(Long commentId) {
        Comment comment = findById(commentId);
        comment.likeCountDown();
    }
}
