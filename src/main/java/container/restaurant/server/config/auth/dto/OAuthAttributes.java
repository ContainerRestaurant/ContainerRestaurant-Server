package container.restaurant.server.config.auth.dto;

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
    private final String nameAttributeKey;
    private final String email;
    private final String profile;

    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes
    ) {
        if ("google".equals(registrationId))
            return ofGoogle(userNameAttributeName, attributes);
        return ofKakao(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    @SuppressWarnings("unchecked")
    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, String> properties = (Map<String, String>) attributes.get("properties");
        Map<String, String> kakaoAccount = (Map<String, String>) attributes.get("kakao_account");

        return OAuthAttributes.builder()
                .email(kakaoAccount.get("email"))
                .profile(properties.get("profile_image"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .profile(profile)
                .build();
    }
}
