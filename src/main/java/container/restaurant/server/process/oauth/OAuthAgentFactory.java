package container.restaurant.server.process.oauth;

import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.process.oauth.apple.AppleOAuthAgent;
import container.restaurant.server.process.oauth.kakao.KakaoOAuthAgent;

import java.util.Locale;
import java.util.Map;

import static container.restaurant.server.domain.user.OAuth2Registration.*;

public class OAuthAgentFactory {

    private final Map<OAuth2Registration, OAuthAgent> agentMap;

    public static OAuthAgentFactory createDefaultFactory() {
        return new OAuthAgentFactory(Map.of(
                KAKAO, new KakaoOAuthAgent(),
                APPLE, new AppleOAuthAgent()
        ));
    }

    private OAuthAgentFactory(Map<OAuth2Registration, OAuthAgent> map) {
        this.agentMap = map;
    }

    public OAuthAgent get(OAuth2Registration provider) {
        return agentMap.get(provider);
    }

    public OAuthAgent get(String provider) {
        return agentMap.get(valueOf(provider.toUpperCase(Locale.ROOT)));
    }
}
