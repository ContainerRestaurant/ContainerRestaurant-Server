package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
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
            @LoginUser SessionUser sessionUser, @RequestBody @Valid UserDto.Create dto
    ) {
        if (sessionUser != null)
            return ResponseEntity.noContent().build();

        SessionUser newSession = SessionUser.from(userService.createFrom(dto));
        setLoginUser(newSession);

        return ResponseEntity
                .created(userLinker.getUserById(newSession.getId()).toUri())
                .header(Header.USER_ID, newSession.getId().toString())
                .build();
    }

    @PostMapping("login")
    public ResponseEntity<?> tokenLogin(
            @LoginUser SessionUser sessionUser, @RequestBody UserDto.TokenLogin dto
    ) {
        if (sessionUser != null)
            return ResponseEntity.noContent().build();

        return userService.tokenLogin(dto)
                .map(info -> {
                    SessionUser user = SessionUser.from(info);
                    setLoginUser(user);
                    return ResponseEntity.ok()
                            .header(Header.USER_ID, user.getId().toString())
                            .body(setLinks(info, info.getId()));
                })
                .orElseThrow(() -> new UnauthorizedException("로그인 실패 - 해당 사용자를 찾을 수 없습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@LoginUser SessionUser sessionUser) {
        return ResponseEntity.of(ofNullable(sessionUser)
                .map(SessionUser::getId)
                .map(id -> setLinks(userService.getUserInfoById(id), id)));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser
    ) {
        Long loginId = sessionUser != null ? sessionUser.getId() : -1;

        return ResponseEntity.ok(
                setLinks(userService.getUserInfoById(id), loginId)
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser,
            @RequestBody UserDto.Update updateDto
    ) {
        if (!id.equals(sessionUser.getId()))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        return ResponseEntity.ok(
                setLinks(userService.update(id, updateDto), sessionUser.getId())
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser
    ) {
        if (!id.equals(sessionUser.getId()))
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
        setLoginUser(SessionUser.from(userService.findById(1L)));
        return ResponseEntity.noContent().build();
    }

    private void setLoginUser(SessionUser newSession) {
        httpSession.setAttribute("user", newSession);
    }

    private void logout() {
        setLoginUser(null);
    }

}
