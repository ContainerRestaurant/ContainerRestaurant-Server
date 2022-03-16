package container.restaurant.server.domain.push;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.exception.UsingPushTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PushTokenService {
    private final PushTokenRepository pushTokenRepository;
    private final UserService userService;

    @Transactional
    public PushToken registerPushToken(String token) {
        return pushTokenRepository.save(new PushToken(token));
    }

    public void deleteToken(Long tokenId) {
        pushTokenRepository.delete(findById(tokenId));
    }

    @Transactional
    public PushToken registerUserPushToken(Long loginId, String token) {
        // 클라이언트 푸시 토큰은 저장 후
        PushToken pushToken = registerPushToken(token);

        if (loginId != null) {
            // 로그인 아이디가 존재할 경우 사용자의 푸시 토큰 업데이트
            // 이미 푸시 토큰이 있는 사용자도 업데이트 될 수 있다.
            User owner = userService.findById(loginId);
            owner.setPushToken(pushToken);
        }

        return pushToken;
    }

    @Transactional
    public void deleteUserPushToken(Long loginId, Long tokenId) {
        if (loginId != null) {
            // 로그인 아이디가 존재할 때 해당 사용자의 푸시 토큰 정보를 제거
            User owner = userService.findById(loginId);
            // 로그인한 사용자의 토큰과 입력된 토큰이 같지 않으면 인증 에러
            if (owner.getPushToken() != null && owner.getPushToken().getId().equals(tokenId))
                owner.setPushToken(null);
            else
                throw new FailedAuthorizationException("다른 사용자의 푸시 토큰은 삭제할 수 없습니다.");
        } else {
            // 비로그인의 경우 토큰을 사용하는 사용자가 있는지 확인
            usingPushTokenCheck(tokenId);
        }
        // 문제 없을경우 푸시토큰 제거
        deleteToken(tokenId);
    }

    private void usingPushTokenCheck(Long tokenId) {
        User owner = userService.findByPushTokenId(tokenId);
        if (owner != null) {
            // 토큰 아이디로 조회된 사용자가 있으면
            throw new UsingPushTokenException("사용 중인 푸시 토큰은 삭제할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public PushToken findById(Long id) {
        return pushTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "푸시 토큰을 찾을 수 없습니다.(id:" + id + ")"));
    }

}
