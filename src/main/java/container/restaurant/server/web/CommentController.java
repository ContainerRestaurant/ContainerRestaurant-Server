package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import container.restaurant.server.web.linker.CommentLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    private final FeedRepository feedRepository;
    private final CommentLinker commentLinker;

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody Comment comment
    ){
        return ResponseEntity.ok(
                EntityModel.of(commentService.createComment(comment))
        );
    }

    @GetMapping("feed/{feedId}")
    public ResponseEntity<?> getCommentByFeed(
            @PathVariable Long feedId,
            @LoginUser SessionUser sessionUser
    ){
        LinkedHashMap<Long, CommentInfoDto> comments = commentService.findAllByFeed(
                feedRepository.findById(feedId).orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"))
                , sessionUser
        );
        comments.values().forEach(dto -> {
            try{
                if(dto.getOwnerId().equals(sessionUser.getId()))
                    commentService.setLinks(dto);
            }catch (NullPointerException e){  }
        });

        return ResponseEntity.ok(
                CollectionModel.of(comments).add(commentLinker.getCommentByFeed(feedId).withSelfRel())
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateCommentById(
            @PathVariable Long id,
            @RequestBody CommentUpdateDto updateDto
    ){
        commentService.update(id, updateDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCommentById(
            @PathVariable Long id
    ){
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
