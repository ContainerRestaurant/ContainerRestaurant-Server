package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import container.restaurant.server.web.linker.CommentLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
        List<Link> links = commentSelfLinks(feedId, sessionUser);

        List<CommentInfoDto> comments = commentService.findAllByFeed(
                feedRepository.findById(feedId).orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"))
                , sessionUser
        );
        comments.forEach(dto -> {
            try{
                if(dto.getOwnerId().equals(sessionUser.getId()))
                    setLinks(dto, feedId);
            }catch (NullPointerException e){  }
        });

        return ResponseEntity.ok(
                CollectionModel.of(comments).add(links)
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

    private CommentController getController(){ return methodOn(CommentController.class); }

    private List<Link> commentInfoLinks(Long id, SessionUser sessionUser){
        List<Link> links = new ArrayList<>();
        links.add(linkTo(getController().getCommentByFeed(id, sessionUser)).withSelfRel());
        links.add(linkTo(getController().createComment(new Comment())).withRel("post-comment"));
        links.add(linkTo(getController().updateCommentById(id, new CommentUpdateDto())).withRel("patch-comment"));
        links.add(linkTo(getController().deleteCommentById(id)).withRel("delete-comment"));

        return links;
    }

    private List<Link> commentSelfLinks(Long id, @LoginUser SessionUser sessionUser){
        List<Link> links = new ArrayList<>();
        links.add(linkTo(getController().getCommentByFeed(id, sessionUser)).withSelfRel());
        return links;
    }

    private CommentInfoDto setLinks(CommentInfoDto dto, Long id){
        return dto
                .add(
                    commentLinker.updateComment(dto.getId()).withRel("patch"),
                    commentLinker.deleteComment(dto.getId()).withRel("delete")
                );
    }
}
