package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.UserController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class UserLinker {

    UserController proxy =
            DummyInvocationUtils.methodOn(UserController.class);

    SessionUser u = new SessionUser();

    public LinkBuilder getUserById(Long userId) {
        return linkTo(proxy.getUserById(userId, u));
    }

    public LinkBuilder updateUserById(Long userId) {
        return linkTo(proxy.updateUserById(userId, u, null));
    }

    public LinkBuilder deleteById(Long userId) {
        return linkTo(proxy.deleteById(userId, u));
    }

    public LinkBuilder existsNickname(String nickname) {
        return linkTo(proxy.existsNickname(nickname));
    }

    public LinkBuilder existsNickname() {
        return existsNickname(null);
    }
}
