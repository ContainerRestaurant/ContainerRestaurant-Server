package container.restaurant.server.web.linker;

import container.restaurant.server.web.PushController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class PushTokenLinker {
    PushController proxy =
            DummyInvocationUtils.methodOn(PushController.class);

    public LinkBuilder registerClientPushToken(String token) {
        return linkTo(proxy.registerClientPushToken(-1L, token));
    }

    public LinkBuilder deleteClientPushToken(Long tokenId) {
        return linkTo(proxy.deleteClientPushToken(-1L, tokenId));
    }


}
