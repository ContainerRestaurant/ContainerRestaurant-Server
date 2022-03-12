package container.restaurant.server.web.linker;

import container.restaurant.server.web.FeedLikeController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class FeedLikeLinker {

    private final FeedLikeController proxy =
            DummyInvocationUtils.methodOn(FeedLikeController.class);

    public LinkBuilder userLikeFeed(Long feedId) {
        return linkTo(proxy.userLikeFeed(-1L, feedId));
    }

    public LinkBuilder userCancelLikeFeed(Long feedId) {
        return linkTo(proxy.userCancelLikeFeed(-1L, feedId));
    }

}
