package container.restaurant.server.web.linker;

import container.restaurant.server.web.StatisticsController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class StatisticsLinker {
    StatisticsController proxy =
            DummyInvocationUtils.methodOn(StatisticsController.class);

    public LinkBuilder getRecentFeedUsers() {
        return linkTo(proxy.getRecentFeedUsers());
    }

    public LinkBuilder getMostFeedUsers() {
        return linkTo(proxy.getMostFeedUsers());
    }
}
