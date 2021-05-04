package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.FeedLikeController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class FeedLikeLinker {

    private final FeedLikeController proxy =
            DummyInvocationUtils.methodOn(FeedLikeController.class);

    private final SessionUser u = new SessionUser();

    public LinkBuilder userLikeFeed(Long feedId) {
        return linkTo(proxy.userLikeFeed(u, feedId));
    }

    public LinkBuilder userCancelLikeFeed(Long feedId) {
        return linkTo(proxy.userCancelLikeFeed(u, feedId));
    }

}
