package container.restaurant.server.process.oauth;

import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.process.oauth.kakao.KakaoOAuthAgent;

import java.util.Optional;

/**
 * OAuth 관련 API 를 처리해주는 인터페이스<p/>
 * 각 Provider 별로 구현체를 가져야한다.<p/>
 * 관련 : {@link KakaoOAuthAgent}
 */
public interface OAuthAgent {

    /**
     * 액세스 토큰을 활용해 {@link OAuthAttributes} 를 생성한다.
     * @param accessToken 사용자의 액세스 토큰
     * @return 사용자 정보가 담긴 {@link OAuthAttributes}에 대한 {@link Optional}
     */
    Optional<OAuthAttributes> getAuthAttrFrom(String accessToken);

    CustomOAuth2User getAuthUserFrom(String accessToken);

}
