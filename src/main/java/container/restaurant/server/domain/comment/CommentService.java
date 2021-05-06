package container.restaurant.server.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

//    public <List>Comment findAllByFeed(){
//
//    }

    @Transactional
    public void deleteById(Long id){ commentRepository.deleteById(id); }

    public void update(){}
}
