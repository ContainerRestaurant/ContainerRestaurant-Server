package container.restaurant.server.web;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("{feedId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long feedId,
            @RequestBody Comment comment
    ){
        // TODO
        return null;
    }

    @GetMapping("{feedId}")
    public ResponseEntity<?> getCommentByFeed(
            @PathVariable Long feedId
    ){
        // TODO
        return null;
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
