package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.CommentLikeController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class CommentLikeLinker {
    private final CommentLikeController proxy
            = DummyInvocationUtils.methodOn(CommentLikeController.class);

    private final SessionUser u
            = DummyInvocationUtils.methodOn(SessionUser.class);

    public LinkBuilder userLikeComment(Long commentId) { return linkTo(proxy.userLikeComment(u, commentId)); }

    public LinkBuilder userCancelLikeComment(Long commentId) { return linkTo(proxy.userCancelLikeComment(u, commentId));}
}
