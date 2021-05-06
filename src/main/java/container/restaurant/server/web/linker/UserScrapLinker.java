package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.UserScrapController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class UserScrapLinker {

    private final UserScrapController proxy =
            DummyInvocationUtils.methodOn(UserScrapController.class);

    private final SessionUser u = new SessionUser();

    public LinkBuilder scrapFeed(Long feedId) {
        return linkTo(proxy.scrapFeed(u, feedId));
    }

    public LinkBuilder cancelScrapFeed(Long feedId) {
        return linkTo(proxy.cancelScrapFeed(u, feedId));
    }

}
