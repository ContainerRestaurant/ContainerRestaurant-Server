package container.restaurant.server.domain.comment;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import container.restaurant.server.web.linker.CommentLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    public List<CommentInfoDto> findAllByFeed(Feed feed, SessionUser sessionUser) throws ResourceNotFoundException{
        List<CommentInfoDto> comment = new ArrayList<>();
        List<Comment> comments = commentRepository.findAllByFeed(feed);
        for(Comment comment1 : comments){
            if(comment1.getUpperReply()!=null){
                for(CommentInfoDto commentInfoDto : comment){
                    if(commentInfoDto.getId().equals(comment1.getUpperReply().getId())){
                        try{
                            if(sessionUser.getId().equals(comment1.getOwner().getId()))
                                commentInfoDto.getCommentReply().add(setLinks(CommentInfoDto.from(comment1)));

                        }catch (NullPointerException e){ commentInfoDto.getCommentReply().add(CommentInfoDto.from(comment1)); }
                    }
                }
            }else{
                comment.add(CommentInfoDto.from(comment1));
            }
        }
        return comment;
    }

    @Transactional
    public void deleteById(Long id){ commentRepository.deleteById(id); }

    @Transactional
    public void update(Long id, CommentUpdateDto commentUpdateDto){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다.(id:"+id+")"));
        commentUpdateDto.updateComment(comment);
    }

    private final CommentLinker commentLinker;
    private CommentInfoDto setLinks(CommentInfoDto dto){
        return dto
                .add(
                        commentLinker.updateComment(dto.getId()).withRel("patch"),
                        commentLinker.deleteComment(dto.getId()).withRel("delete")
                );
    }
}
