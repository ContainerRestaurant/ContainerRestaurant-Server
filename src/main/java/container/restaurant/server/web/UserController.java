package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.constant.Header;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.UnauthorizedException;
import container.restaurant.server.web.dto.user.UserDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;
    private final RestaurantFavoriteLinker restaurantFavoriteLinker;

    @PostMapping
    public ResponseEntity<?> createWithToken(
            @LoginId Long loginId, @RequestBody @Valid UserDto.Create dto
    ) {
        if (loginId != null)
            return ResponseEntity.noContent().build();

        UserDto.Info newUser = userService.createFrom(dto);
        setLoginUser(newUser.getId());

        return ResponseEntity
                .created(userLinker.getUserById(newUser.getId()).toUri())
                .header(Header.USER_ID, newUser.getId().toString())
                .build();
    }

    @PostMapping("login")
    public ResponseEntity<?> tokenLogin(
            @LoginId Long loginId, @RequestBody UserDto.TokenLogin dto
    ) {
        if (loginId != null)
            return ResponseEntity.noContent().build();

        return userService.tokenLogin(dto)
                .map(info -> {
                    setLoginUser(info.getId());
                    return ResponseEntity.ok()
                            .header(Header.USER_ID, info.getId().toString())
                            .body(setLinks(info, info.getId()));
                })
                .orElseThrow(() -> new UnauthorizedException("로그인 실패 - 해당 사용자를 찾을 수 없습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@LoginId Long loginId) {
        return ResponseEntity.of(ofNullable(loginId)
                .map(id -> setLinks(userService.getUserInfoById(id), id)));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(userService.getUserInfoById(id), loginId)
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginId Long loginId,
            @RequestBody UserDto.Update updateDto
    ) {
        if (!id.equals(loginId))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        return ResponseEntity.ok(
                setLinks(userService.update(id, updateDto), id)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Long id, @LoginId Long loginId
    ) {
        if (!id.equals(loginId))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        userService.deleteById(id);
        logout();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("nickname/exists")
    public ResponseEntity<?> existsNickname(
            @NicknameConstraint @RequestParam String nickname
    ) {
        return ResponseEntity.ok(
                setLinks(UserDto.NicknameExists.of(nickname, userService.existsUserByNickname(nickname)))
        );
    }

    private UserDto.NicknameExists setLinks(UserDto.NicknameExists dto) {
        return dto.add(userLinker.existsNickname(dto.getNickname()).withSelfRel());
    }

    private UserDto.Info setLinks(UserDto.Info dto, Long loginId) {
        return dto
                .add(
                        userLinker.getUserById(dto.getId()).withSelfRel(),
                        feedLinker.selectUserFeed(dto.getId()).withRel("feeds")
                )
                .addAllIf(loginId.equals(dto.getId()), () -> List.of(
                        userLinker.updateUserById(dto.getId()).withRel("patch"),
                        userLinker.deleteById(dto.getId()).withRel("delete"),
                        userLinker.existsNickname().withRel("nickname-exists"),
                        feedLinker.selectUserScrapFeed(dto.getId()).withRel("scraps"),
                        restaurantFavoriteLinker.findAllByUser().withRel("restaurant-favorite")
                ));
    }

    // FIXME 임시 로그인 방편
    private final HttpSession httpSession;
    @GetMapping("temp-login")
    public ResponseEntity<?> tempLogin() {
        setLoginUser(1L);
        return ResponseEntity.noContent().build();
    }

    private void setLoginUser(Long loginId) {
        httpSession.setAttribute("userId", loginId);
    }

    private void logout() {
        setLoginUser(null);
    }

}
