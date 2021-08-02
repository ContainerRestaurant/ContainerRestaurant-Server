package container.restaurant.server.web.linker;

import container.restaurant.server.web.ScrapFeedController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ScrapFeedLinker {

    private final ScrapFeedController proxy =
            DummyInvocationUtils.methodOn(ScrapFeedController.class);

    public LinkBuilder scrapFeed(Long feedId) {
        return linkTo(proxy.scrapFeed(-1L, feedId));
    }

    public LinkBuilder cancelScrapFeed(Long feedId) {
        return linkTo(proxy.cancelScrapFeed(-1L, feedId));
    }

}
