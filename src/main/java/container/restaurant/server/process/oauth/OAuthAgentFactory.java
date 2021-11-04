package container.restaurant.server.process.oauth;

import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.process.oauth.kakao.KakaoOAuthAgent;

import java.util.Locale;
import java.util.Map;

public class OAuthAgentFactory {

    private final Map<OAuth2Registration, OAuthAgent> agentMap;

    public static OAuthAgentFactory createDefaultFactory() {
        return new OAuthAgentFactory(Map.of(
                OAuth2Registration.KAKAO, new KakaoOAuthAgent()
        ));
    }

    private OAuthAgentFactory(Map<OAuth2Registration, OAuthAgent> map) {
        this.agentMap = map;
    }

    public OAuthAgent get(OAuth2Registration provider) {
        return agentMap.get(provider);
    }

    public OAuthAgent get(String provider) {
        return agentMap.get(OAuth2Registration.valueOf(provider.toUpperCase(Locale.ROOT)));
    }
}
