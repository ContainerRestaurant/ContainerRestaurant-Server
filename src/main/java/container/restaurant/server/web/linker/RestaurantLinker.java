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
        return linkTo(proxy.findById(id));
    }

}
