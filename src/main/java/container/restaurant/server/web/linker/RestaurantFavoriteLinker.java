package container.restaurant.server.web.linker;

import container.restaurant.server.web.RestaurantFavoriteController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class RestaurantFavoriteLinker {

    private final RestaurantFavoriteController proxy =
            DummyInvocationUtils.methodOn(RestaurantFavoriteController.class);

    public LinkBuilder userFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userFavoriteRestaurant(-1L, restaurantId));
    }

    public LinkBuilder userCancelFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userCancelFavoriteRestaurant(-1L, restaurantId));
    }

    public LinkBuilder findAllByUser() {
        return linkTo(proxy.findAllByUser(-1L));
    }

}
