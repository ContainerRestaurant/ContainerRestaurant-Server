package container.restaurant.server.web;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody Comment comment
    ){
//        Feed feed = feedRepository.findById(feedId)
//                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"));
//        List<Link> links = commentInfoLinks();

        return ResponseEntity.ok(
                EntityModel.of(commentService.createComment(comment))
        );
    }

    @GetMapping("{feedId}")
    public ResponseEntity<?> getCommentByFeed(
            @PathVariable Long feedId
    ){
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게시글입니다.(id:"+feedId+")"));
        List<Link> links = commentInfoLinks(feedId);

        return ResponseEntity.ok(
                EntityModel.of(commentService.findAllByFeed(feed)).add(links)
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

    private List<Link> commentInfoLinks(Long id){
        List<Link> links = new ArrayList<>();
        links.add(linkTo(getController().getCommentByFeed(id)).withSelfRel());
        links.add(linkTo(getController().createComment(new Comment())).withRel("post-comment"));
        links.add(linkTo(getController().updateCommentById(id, new CommentUpdateDto())).withRel("patch-comment"));
        links.add(linkTo(getController().deleteCommentById(id)).withRel("delete-comment"));

        return links;
    }
}
