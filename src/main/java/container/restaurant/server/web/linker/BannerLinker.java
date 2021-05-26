package container.restaurant.server.web.linker;

import container.restaurant.server.web.IndexController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class BannerLinker {
    IndexController proxy
            = DummyInvocationUtils.methodOn(IndexController.class);

    public LinkBuilder getBanners(){ return linkTo(proxy.getBanners());}
}
