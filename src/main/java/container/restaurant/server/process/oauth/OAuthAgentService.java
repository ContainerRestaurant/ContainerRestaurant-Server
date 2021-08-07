package container.restaurant.server.process.oauth;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.exception.UnauthorizedException;
import container.restaurant.server.web.dto.user.UserDto;

public class OAuthAgentService {

    private final static OAuthAgentFactory DEFAULT_OAUTH_AGENT_FACTORY =
            OAuthAgentFactory.createDefaultFactory();

    private final OAuthAgentFactory agentFactory;

    public OAuthAgentService(OAuthAgentFactory agentFactory) {
        this.agentFactory = agentFactory != null ? agentFactory : DEFAULT_OAUTH_AGENT_FACTORY;
    }

    public CustomOAuth2User getAuthUser(UserDto.ToRequestToken dto) {
        try {
            return agentFactory.get(dto.getProvider())
                    .getAuthUserFrom(dto.getAccessToken());
        } catch (RuntimeException ex) {
            throw new UnauthorizedException("액세스 토큰 인증에 실패했습니다.", ex);
        }
    }

}
