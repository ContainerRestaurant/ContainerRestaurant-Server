package container.restaurant.server.web.dto.user;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.ResponseDtoAssembler;
import container.restaurant.server.web.linker.UserControllerLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class UserInfoDtoAssembler implements ResponseDtoAssembler<User, UserInfoDto> {

    private final UserControllerLinker userControllerLinker;

    @Override
    public Function<User, UserInfoDto> converter() {
        return UserInfoDto::from;
    }

    @Override
    public Iterable<Link> links(User entity) {
        return List.of(userControllerLinker.getUserById(entity.getId()).withSelfRel());
    }

    @Override
    public Iterable<Link> authLinks(User entity) {
        return List.of(
                userControllerLinker.updateUserById(entity.getId()).withRel("patch-user"),
                userControllerLinker.deleteById(entity.getId()).withRel("delete-user"),
                userControllerLinker.existsNickname().withRel("check-nickname-exists")
        );
    }
}
