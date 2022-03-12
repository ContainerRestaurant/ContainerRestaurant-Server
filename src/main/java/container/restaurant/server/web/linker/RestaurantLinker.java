package container.restaurant.server.web.linker;

import container.restaurant.server.web.RestaurantController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class RestaurantLinker {

    RestaurantController proxy =
            DummyInvocationUtils.methodOn(RestaurantController.class);

    public LinkBuilder findById(Long id) {
        return linkTo(proxy.findById(id, -1L));
    }

    public LinkBuilder findNearByRestaurants(double latitude, double longitude, long radius) {
        return linkTo(proxy.findNearByRestaurants(latitude, longitude, radius, -1L));
    }

    public LinkBuilder findNearByRestaurants() {
        return linkTo(proxy.findNearByRestaurants(null, null, null, -1L));
    }

    public LinkBuilder updateVanish(Long id) {
        return linkTo(proxy.updateVanish(id));
    }

}
