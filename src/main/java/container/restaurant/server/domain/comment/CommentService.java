package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
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

    @Transactional
    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
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
    public void deleteById(Long id){ commentRepository.deleteById(id); }

    @Transactional
    public void update(Long id, CommentUpdateDto commentUpdateDto){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:"+id+")"));
        commentUpdateDto.updateComment(comment);
    }
}
