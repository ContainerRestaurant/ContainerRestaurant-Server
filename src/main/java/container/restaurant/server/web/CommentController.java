package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
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

        // TODO 대댓글이 있는 댓글 삭제시 본 댓글은 안보이고 대댓글만 보이도록 GET 수정
        return ResponseEntity.ok(
                setLinks(commentService.findAllByFeed(feedId), userID)
                        .add(commentLinker.getCommentByFeed(feedId).withSelfRel())
        );
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<?> updateCommentById(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateDto updateDto,
            @LoginUser SessionUser sessionUser
    ){
        return ResponseEntity.ok(
                setLinks(commentService.update(commentId, updateDto, sessionUser.getId()))
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCommentById(
            @PathVariable Long id,
            @LoginUser SessionUser sessionUser
    ){
        commentService.deleteById(id, sessionUser.getId());
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
        boolean isOwner = dto.getOwnerId().equals(userId);
        dto
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
