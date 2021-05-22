package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.ScrapFeedController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ScrapFeedLinker {

    private final ScrapFeedController proxy =
            DummyInvocationUtils.methodOn(ScrapFeedController.class);

    private final SessionUser u =
            DummyInvocationUtils.methodOn(SessionUser.class);

    public LinkBuilder scrapFeed(Long feedId) {
        return linkTo(proxy.scrapFeed(u, feedId));
    }

    public LinkBuilder cancelScrapFeed(Long feedId) {
        return linkTo(proxy.cancelScrapFeed(u, feedId));
    }

}
