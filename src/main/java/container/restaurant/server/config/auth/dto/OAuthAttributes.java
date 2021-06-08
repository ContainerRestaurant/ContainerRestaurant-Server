package container.restaurant.server.config.auth.dto;

import container.restaurant.server.domain.user.AuthProvider;
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

    private final String authId;
    private final String nickname;
    private final String email;
    private final AuthProvider provider;

    private final String nameAttributeKey;

    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes
    ) {
        if ("google".equals(registrationId))
            return ofGoogle(userNameAttributeName, attributes);
        return ofKakao(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider(AuthProvider.GOOGLE)
                .authId(attributes.get("sub").toString())
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .nickname((String) attributes.get("name"))
                .build();
    }

    public static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        return ofKakao(null, attributes);
    }

    @SuppressWarnings("unchecked")
    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, String> properties = (Map<String, String>) attributes.get("properties");
        Map<String, String> kakaoAccount = (Map<String, String>) attributes.get("kakao_account");

        return OAuthAttributes.builder()
                .provider(AuthProvider.KAKAO)
                .authId(attributes.get("id").toString())
                .email(kakaoAccount.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .nickname(properties.get("nickname"))
                .build();
    }

    public User toEntity() {
        return User.builder()
                .authProvider(provider)
                .authId(authId)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
