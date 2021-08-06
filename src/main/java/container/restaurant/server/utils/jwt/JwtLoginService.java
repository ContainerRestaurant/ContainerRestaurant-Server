package container.restaurant.server.utils.jwt;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public interface JwtLoginService {

    String tokenize(OAuth2User user);

    CustomOAuth2User parse(String token);

}
