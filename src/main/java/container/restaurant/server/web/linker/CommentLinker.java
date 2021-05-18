package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.CommentController;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class CommentLinker {

    CommentController proxy =
            DummyInvocationUtils.methodOn(CommentController.class);

    CommentUpdateDto commentUpdateDto =
            DummyInvocationUtils.methodOn(CommentUpdateDto.class);

    CommentCreateDto commentCreateDto=
            DummyInvocationUtils.methodOn(CommentCreateDto.class);

    SessionUser u =
            DummyInvocationUtils.methodOn(SessionUser.class);

    public LinkBuilder getCommentByFeed(Long id){ return linkTo(proxy.getCommentByFeed(id, u)); }

    public LinkBuilder updateComment(Long id){
        return linkTo(proxy.updateCommentById(id, commentUpdateDto, u));
    }

    public LinkBuilder deleteComment(Long id){
        return linkTo(proxy.deleteCommentById(id, u));
    }

    public LinkBuilder createComment(Long id) { return linkTo(proxy.createComment(commentCreateDto,id,u)); }
}
