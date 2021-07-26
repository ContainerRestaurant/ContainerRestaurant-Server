package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.push.event.FeedCommentedEvent;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exception.ResourceNotFoundException;
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
import java.util.Set;

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
        Feed feed = feedService.findById(feedId);

        LinkedHashMap<Long, CommentInfoDto> commentDtoMap = new LinkedHashMap<>();

        List<Comment> commentList = commentRepository.findAllByFeed(feed);

        Set<Long> likeIds = ofNullable(userId)
                .map(id -> commentLikeRepository.test(id, commentList))
                .orElseGet(Set::of);

        commentList.forEach(comment ->
                ofNullable(comment.getUpperReply()).ifPresentOrElse(
                        upperReply -> commentDtoMap.get(upperReply.getId())
                                .addCommentReply(CommentInfoDto.from(comment, likeIds.contains(comment.getId()))),
                        () -> commentDtoMap.put(comment.getId(),
                                CommentInfoDto.from(comment, likeIds.contains(comment.getId())))
                ));

        return CollectionModel.of(commentDtoMap.values());
    }

    @Transactional
    public void deleteById(Long id, Long userId) {
        Comment comment = findById(id);

        if (!comment.getOwner().getId().equals(userId))
            throw new ResourceNotFoundException("삭제 할 수 있는 유저가 아닙니다.");

        // 대댓글 있다면(hasReply) 댓글 isDeleted 처리
        if (comment.getHasReply()) {
            comment.setIsDeleted();
        } else {
            ofNullable(comment.getUpperReply())
                    .filter(upperReply ->
                            commentRepository.findAllByUpperReplyId(upperReply.getId()).size() == 1)
                    .ifPresent(upperReply -> {
                        if (upperReply.getIsDeleted()) {
                            commentRepository.delete(upperReply);
                        } else {
                            upperReply.unsetHasReply();
                        }
                    });
            commentRepository.delete(comment);
        }
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
