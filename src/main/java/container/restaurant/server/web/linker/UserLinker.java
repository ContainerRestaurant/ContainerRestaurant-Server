package container.restaurant.server.web.linker;

import container.restaurant.server.web.UserController;
import container.restaurant.server.web.dto.user.UserDto;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class UserLinker {

    UserController proxy =
            DummyInvocationUtils.methodOn(UserController.class);

    UserDto.Update updateDto =
            DummyInvocationUtils.methodOn(UserDto.Update.class);

    public LinkBuilder getUserById(Long userId) {
        return linkTo(proxy.getUserById(userId, -1L));
    }

    public LinkBuilder updateUserById(Long userId) {
        return linkTo(proxy.updateUserById(userId, -1L, updateDto));
    }

    public LinkBuilder deleteById(Long userId) {
        return linkTo(proxy.deleteById(userId, -1L));
    }

    public LinkBuilder existsNickname(String nickname) {
        return linkTo(proxy.existsNickname(nickname));
    }

    public LinkBuilder existsNickname() {
        return existsNickname(null);
    }

    public LinkBuilder getCurrentUser() {
        return linkTo(proxy.getCurrentUser(-1L));
    }
}
