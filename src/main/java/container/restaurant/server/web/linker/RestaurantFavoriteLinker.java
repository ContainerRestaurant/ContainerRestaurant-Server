package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.ImageController;
import container.restaurant.server.web.RestaurantController;
import container.restaurant.server.web.RestaurantFavoriteController;
import container.restaurant.server.web.dto.restaurant.RestaurantNearInfoDto;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class RestaurantFavoriteLinker {

    private final RestaurantFavoriteController proxy =
            DummyInvocationUtils.methodOn(RestaurantFavoriteController.class);

    private final RestaurantController restaurantProxy =
            DummyInvocationUtils.methodOn(RestaurantController.class);

    private final SessionUser u = new SessionUser();

    public LinkBuilder userFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userFavoriteRestaurant(u, restaurantId));
    }

    public LinkBuilder userCancelFavoriteRestaurant(Long restaurantId) {
        return linkTo(proxy.userCancelFavoriteRestaurant(u, restaurantId));
    }

    public LinkBuilder userFindAllFavoriteRestaurant() {
        return linkTo(proxy.userFindAllFavoriteRestaurant(u));
    }

    public LinkBuilder findRestaurantById(RestaurantNearInfoDto restaurantNearInfoDto) {
        return linkTo(restaurantProxy.findById(restaurantNearInfoDto.getId()));
    }

    public LinkBuilder restaurantImagePath(String imagePath) {
        return linkTo(ImageController.class).slash(imagePath);
    }
}
