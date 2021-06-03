package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.RestaurantFavoriteController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class RestaurantFavoriteLinker {

    private final RestaurantFavoriteController proxy =
            DummyInvocationUtils.methodOn(RestaurantFavoriteController.class);

    private final SessionUser u =
            DummyInvocationUtils.methodOn(SessionUser.class);

    public LinkBuilder userFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userFavoriteRestaurant(u, restaurantId));
    }

    public LinkBuilder userCancelFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userCancelFavoriteRestaurant(u, restaurantId));
    }

    public LinkBuilder findAllByUser() {
        return linkTo(proxy.findAllByUser(u));
    }

}
