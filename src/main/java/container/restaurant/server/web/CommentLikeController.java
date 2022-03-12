package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.comment.like.CommentLikeService;
import container.restaurant.server.web.linker.CommentLikeLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/like/comment")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;
    private final CommentLikeLinker commentLikeLinker;

    @PostMapping("{commentId}")
    public ResponseEntity<?> userLikeComment(
            @LoginId Long loginId,
            @PathVariable Long commentId
    ){
        commentLikeService.userLikeComment(loginId, commentId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                    .add(commentLikeLinker.userLikeComment(commentId).withSelfRel())
                    .add(commentLikeLinker.userCancelLikeComment(commentId).withRel("cancel-like"))
        );
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<?> userCancelLikeComment(
            @LoginId Long loginId,
            @PathVariable Long commentId
    ){
        commentLikeService.userCancelLikeComment(loginId, commentId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                    .add(commentLikeLinker.userCancelLikeComment(commentId).withSelfRel())
                    .add(commentLikeLinker.userLikeComment(commentId).withRel("like"))
        );
    }
}
