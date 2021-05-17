package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import container.restaurant.server.web.linker.CommentLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    private final CommentLinker commentLinker;

    @PostMapping("{feedId}")
    public ResponseEntity<?> createComment(
            @RequestBody CommentCreateDto commentCreateDto,
            @PathVariable Long feedId,
            @LoginUser SessionUser sessionUser
    ){
        CommentInfoDto commentInfoDto = commentService.createComment(commentCreateDto, feedId, sessionUser.getId());
        return ResponseEntity.ok(
                setLinks(commentInfoDto)
        );
    }

    @GetMapping("feed/{feedId}")
    public ResponseEntity<?> getCommentByFeed(
            @PathVariable Long feedId,
            @LoginUser SessionUser sessionUser
    ){
        Long userID = sessionUser != null ? sessionUser.getId() : null;

        return ResponseEntity.ok(
                setLinks(commentService.findAllByFeed(feedId), userID)
                        .add(commentLinker.getCommentByFeed(feedId).withSelfRel())
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

    private CollectionModel<CommentInfoDto> setLinks(CollectionModel<CommentInfoDto> collection, Long userId){
        collection.forEach(dto -> {
            setLinks(dto, userId);
            dto.ifHasReply(commentInfoDto -> setLinks(commentInfoDto, userId));
        });

        return collection;
    }

    private void setLinks(CommentInfoDto dto, Long userId){
        dto.addAllIf(dto.getOwnerId().equals(userId), () -> List.of(
                commentLinker.updateComment(dto.getId()).withRel("patch"),
                commentLinker.deleteComment(dto.getId()).withRel("delete"))
        );
    }

    private CommentInfoDto setLinks(CommentInfoDto commentInfoDto){
        return commentInfoDto
                .add(
                        commentLinker.updateComment(commentInfoDto.getId()).withRel("patch"),
                        commentLinker.deleteComment(commentInfoDto.getId()).withRel("delete")
                );
    }
}
