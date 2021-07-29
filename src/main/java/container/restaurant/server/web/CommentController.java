package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import container.restaurant.server.web.linker.CommentLikeLinker;
import container.restaurant.server.web.linker.CommentLinker;
import container.restaurant.server.web.linker.ReportLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
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
    private final ReportLinker reportLinker;
    private final CommentLikeLinker commentLikeLinker;

    @PostMapping("feed/{feedId}")
    public ResponseEntity<?> createComment(
            @RequestBody CommentCreateDto commentCreateDto,
            @PathVariable Long feedId,
            @LoginId Long loginId
    ){
        Long newCommentId = commentService.createComment(commentCreateDto, feedId, loginId);
        CommentInfoDto commentInfoDto = commentService.get(newCommentId);
        return ResponseEntity.ok(
                setLinks(commentInfoDto)
        );
    }

    @GetMapping("feed/{feedId}")
    public ResponseEntity<?> getCommentByFeed(
            @PathVariable Long feedId,
            @LoginId Long loginId
    ){
        return ResponseEntity.ok(
                setLinks(commentService.findAllByFeed(loginId, feedId), loginId)
                        .add(commentLinker.getCommentByFeed(feedId).withSelfRel())
        );
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<?> updateCommentById(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateDto updateDto,
            @LoginId Long loginId
    ){
        commentService.update(commentId, updateDto, loginId);

        return ResponseEntity.ok(
                setLinks(commentService.get(commentId)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCommentById(
            @PathVariable Long id,
            @LoginId Long loginId
    ){
        commentService.deleteById(id, loginId);
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
        if(dto.getOwnerId() == null) return;
        boolean isOwner = dto.getOwnerId().equals(userId);
        dto
                .add(
                        dto.getIsLike() ?
                                commentLikeLinker.userCancelLikeComment(dto.getId()).withRel("cancel-like") :
                                commentLikeLinker.userLikeComment(dto.getId()).withRel("like")
                )
                .addAllIf(isOwner, () -> List.of(
                        commentLinker.updateComment(dto.getId()).withRel("patch"),
                        commentLinker.deleteComment(dto.getId()).withRel("delete")))
                .addAllIf(!isOwner, () -> List.of(
                        reportLinker.reportComment(dto.getId()).withRel("report")
                ));
    }

    private CommentInfoDto setLinks(CommentInfoDto commentInfoDto){
        return commentInfoDto
                .add(
                        commentLinker.updateComment(commentInfoDto.getId()).withRel("patch"),
                        commentLinker.deleteComment(commentInfoDto.getId()).withRel("delete")
                );
    }
}
