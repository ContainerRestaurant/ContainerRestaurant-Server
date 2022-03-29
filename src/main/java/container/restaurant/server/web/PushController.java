package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.push.PushTokenService;
import container.restaurant.server.web.linker.PushTokenLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushTokenService pushTokenService;
    private final PushTokenLinker pushTokenLinker;

    /*
     * [ 비로그인 허용 ]
     *
     * 클라이언트의 푸시 토큰을 저장하기 위한 용도
     *
     * 세션 정보가 있을경우 사용자의 pushToken 정보 컨트롤
     *
     * @param token
     * @return
     */
    @PostMapping("/token/{token}")
    public ResponseEntity<?> registerClientPushToken(
            @LoginId Long loginId,
            @PathVariable String token) {
        PushToken pushToken = pushTokenService.registerUserPushToken(loginId, token);
        return ResponseEntity.ok(EntityModel.of(pushToken)
                .add(pushTokenLinker.registerClientPushToken(token).withSelfRel())
                .add(pushTokenLinker.deleteClientPushToken(pushToken.getId()).withRel("delete-token"))
        );
    }

    /*
     * [ 비로그인 허용 ]
     *
     * 저장된 클라이언트 푸시 토큰을 삭제 하기 위한 용도
     *
     * 세션 정보가 있을경우 사용자의 pushToken 정보 컨트롤
     *
     * @param sessionUser
     * @return
     */
    @DeleteMapping("/token/{tokenId}")
    public ResponseEntity<?> deleteClientPushToken(
            @LoginId Long loginId,
            @PathVariable Long tokenId) {
        pushTokenService.deleteUserPushToken(loginId, tokenId);
        return ResponseEntity.noContent().build();
    }

}
