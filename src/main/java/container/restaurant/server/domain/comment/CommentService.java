package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

    @Transactional
    public List<Comment> findAllByFeed(Feed feed){
        return commentRepository.findAllByFeed(feed);
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
