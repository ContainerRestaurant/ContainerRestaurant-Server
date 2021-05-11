package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.FeedController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class FeedLinker {

    FeedController proxy =
            DummyInvocationUtils.methodOn(FeedController.class);

    SessionUser u = new SessionUser();

    public LinkBuilder getFeedDetail(Long feedId) {
        return linkTo(proxy.getFeedDetail(feedId, u));
    }

    public LinkBuilder selectFeed(Pageable pageable) {
        return linkTo(proxy.selectFeed(pageable));
    }

    public LinkBuilder selectFeed() {
        return selectFeed(Pageable.unpaged());
    }

    public LinkBuilder selectUserFeed(Long userId, Pageable pageable) {
        return linkTo(proxy.selectUserFeed(userId, pageable));
    }

    public LinkBuilder selectUserFeed(Long userId) {
        return selectUserFeed(userId, Pageable.unpaged());
    }

    public LinkBuilder selectUserScrapFeed(Long userId, Pageable pageable) {
        return linkTo(proxy.selectUserScrapFeed(userId, pageable));
    }

    public LinkBuilder selectUserScrapFeed(Long userId) {
        return selectUserScrapFeed(userId, Pageable.unpaged());
    }

    public LinkBuilder selectRestaurantFeed(Long restaurantId, Pageable pageable) {
        return linkTo(proxy.selectRestaurantFeed(restaurantId, pageable));
    }

    public LinkBuilder selectRestaurantFeed(Long restaurantId) {
        return selectRestaurantFeed(restaurantId, Pageable.unpaged());
    }

    public LinkBuilder createFeed() {
        return linkTo(proxy.createFeed(u));
    }

    public LinkBuilder deleteFeed(Long feedId) {
        return linkTo(proxy.deleteFeed(u, feedId));
    }

    public LinkBuilder updateFeed(Long feedId) {
        return linkTo(proxy.updateFeed(u, feedId));
    }

}
