package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentInfoDto createComment(CommentCreateDto commentCreateDto, Long feedId, Long userId){
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:" +feedId+")"));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 유저입니다.(id:"+userId+")"));

        Comment comment;
        if(commentCreateDto.getUpperReplyId() == null){
            comment = new Comment(user, feed, commentCreateDto.getContent());
        }else{
            Comment upperReply = commentRepository.findById(commentCreateDto.getUpperReplyId())
                    .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"));
            upperReply.setIsHaveReply();
            comment = new Comment(commentCreateDto, feed, user, upperReply);
        }

        return CommentInfoDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public CollectionModel<CommentInfoDto> findAllByFeed(Long feedId) throws ResourceNotFoundException{
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"));

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
    public void deleteById(Long id, Long userId){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:"+id+")"));

        if(!comment.getOwner().getId().equals(userId))
            throw new ResourceNotFoundException("삭제 할 수 있는 유저가 아닙니다.");

        commentRepository.deleteById(id);
    }

    @Transactional
    public CommentInfoDto update(Long commentId, CommentUpdateDto commentUpdateDto, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:"+commentId+")"));

        if(!comment.getOwner().getId().equals(userId))
            throw new ResourceNotFoundException("수정 할 수 있는 유저가 아닙니다.");

        return CommentInfoDto.from(comment.setContent(commentUpdateDto.getContent()));
    }
}
