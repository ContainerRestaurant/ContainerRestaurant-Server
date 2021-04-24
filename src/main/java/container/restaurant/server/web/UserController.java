package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.user.NicknameExistsDto;
import container.restaurant.server.web.dto.user.UserInfoDto;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser
    ) {
        UserInfoDto dto = userService.findById(id);
        List<Link> links = userInfoLinks(id, sessionUser);

        return ResponseEntity.ok(
                EntityModel.of(dto).add(links));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser,
            @RequestBody UserUpdateDto updateDto
    ) {
        if (!id.equals(sessionUser.getId()))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        UserInfoDto dto = userService.update(id, updateDto);
        List<Link> links = userInfoLinks(id, sessionUser);

        return ResponseEntity.ok(
                EntityModel.of(dto).add(links));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser
    ) {
        if (!id.equals(sessionUser.getId()))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("nickname/exists")
    public ResponseEntity<?> existsNickname(
            @NicknameConstraint @RequestParam String nickname
    ) {
        return ResponseEntity.ok().body(
                EntityModel.of(NicknameExistsDto.of(nickname, userService.existsUserByNickname(nickname)))
                        .add(linkTo(getController().existsNickname(nickname)).withSelfRel()));
    }

    private UserController getController() {
        return methodOn(UserController.class);
    }

    private List<Link> userInfoLinks(Long id, SessionUser sessionUser) {
        List<Link> links = new ArrayList<>();
        links.add(linkTo(getController().getUserById(id, sessionUser)).withSelfRel());
        if (id.equals(sessionUser.getId())) {
            links.add(linkTo(getController().updateUserById(id, sessionUser, new UserUpdateDto()))
                    .withRel("patch-user"));
            links.add(linkTo(getController().deleteById(id, sessionUser))
                    .withRel("delete-user"));
            links.add(linkTo(getController().existsNickname(null))
                    .withRel("check-nickname-exists"));
        }
        return links;
    }

}
