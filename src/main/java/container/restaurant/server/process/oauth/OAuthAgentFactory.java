package container.restaurant.server.process.oauth;

import container.restaurant.server.domain.user.AuthProvider;

import java.util.Locale;
import java.util.Map;

public class OAuthAgentFactory {

    private final Map<AuthProvider, OAuthAgent> agentMap;

    public static OAuthAgentFactory createDefaultFactory() {
        return new OAuthAgentFactory(Map.of(
                AuthProvider.KAKAO, new KakaoOAuthAgent()
        ));
    }

    private OAuthAgentFactory(Map<AuthProvider, OAuthAgent> map) {
        this.agentMap = map;
    }

    public OAuthAgent get(AuthProvider provider) {
        return agentMap.get(provider);
    }

    public OAuthAgent get(String provider) {
        return agentMap.get(AuthProvider.valueOf(provider.toUpperCase(Locale.ROOT)));
    }
}
