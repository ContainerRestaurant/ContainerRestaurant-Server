package container.restaurant.server.config.auth.dto;

import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.OAuth2Identifier;
import container.restaurant.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class OAuthAttributes {

    private final Map<String, Object> attributes;

    private final String email;
    private final OAuth2Identifier identifier;

    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes
    ) {
        if ("google".equals(registrationId))
            return ofGoogle(userNameAttributeName, attributes);
        return ofKakao(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .identifier(OAuth2Identifier.of(attributes.get("sub").toString(), OAuth2Registration.GOOGLE))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .build();
    }

    public static OAuthAttributes ofApple(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .identifier(OAuth2Identifier.of(attributes.get("sub").toString(), OAuth2Registration.APPLE))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .build();
    }

    public static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        return ofKakao(null, attributes);
    }

    @SuppressWarnings("unchecked")
    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, String> properties = (Map<String, String>) attributes.getOrDefault("properties", Map.of());
        Map<String, String> kakaoAccount = (Map<String, String>) attributes.getOrDefault("kakao_account", Map.of());

        return OAuthAttributes.builder()
                .identifier(OAuth2Identifier.of(attributes.get("id").toString(), OAuth2Registration.KAKAO))
                .email(kakaoAccount.get("email"))
                .attributes(attributes)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .identifier(identifier)
                .email(email)
                .build();
    }
}
