package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.user.NicknameExistsDto;
import container.restaurant.server.web.dto.user.UserInfoDto;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser
    ) {
        Long loginId = sessionUser != null ? sessionUser.getId() : -1;

        return ResponseEntity.ok(
                setLinks(userService.findById(id), loginId)
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginUser SessionUser sessionUser,
            @RequestBody UserUpdateDto updateDto
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

        return ResponseEntity.noContent().build();
    }

    @GetMapping("nickname/exists")
    public ResponseEntity<?> existsNickname(
            @NicknameConstraint @RequestParam String nickname
    ) {
        return ResponseEntity.ok(
                setLinks(NicknameExistsDto.of(nickname, userService.existsUserByNickname(nickname)))
        );
    }

    private NicknameExistsDto setLinks(NicknameExistsDto dto) {
        return dto.add(userLinker.existsNickname(dto.getNickname()).withSelfRel());
    }

    private UserInfoDto setLinks(UserInfoDto dto, Long loginId) {
        return dto
                .add(
                        userLinker.getUserById(dto.getId()).withSelfRel(),
                        feedLinker.selectUserFeed(dto.getId()).withRel("feeds")
                )
                .addAllIf(loginId.equals(dto.getId()), () -> List.of(
                        userLinker.updateUserById(dto.getId()).withRel("patch"),
                        userLinker.deleteById(dto.getId()).withRel("delete"),
                        userLinker.existsNickname().withRel("nickname-exists"),
                        feedLinker.selectUserScrapFeed(dto.getId()).withRel("scraps")
                        // TODO favorite link
                ));
    }

}
