package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        Boolean self = sessionUser.getId().equals(id);
        return ResponseEntity.ok(userService.findById(id, self));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser,
            @RequestBody UserUpdateDto updateDto
    ) {
        if (!id.equals(sessionUser.getId()))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        return ResponseEntity.ok(userService.update(id, updateDto));
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
        return ResponseEntity.ok(userService.existsUserByNickname(nickname));
    }

}
