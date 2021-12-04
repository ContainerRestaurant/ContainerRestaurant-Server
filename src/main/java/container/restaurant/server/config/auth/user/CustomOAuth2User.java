package container.restaurant.server.config.auth.user;

import container.restaurant.server.domain.user.OAuth2Identifier;
import container.restaurant.server.domain.user.OAuth2Registration;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
public class CustomOAuth2User implements OAuth2User, Serializable {

    //=== attributes 속성(일부는 JWT(RFC 7519) 스펙) ===//

    public static final String SUBJECT = "sub";
    public static final String EXPIRATION_TIME = "exp";
    public static final String ISSUED_AT = "iat";
    public static final String EMAIL = "email";
    public static final String REGISTRATION = "registration";

    public static final List<String> REQUIRED_ATTRIBUTES = List.of(SUBJECT, REGISTRATION, EXPIRATION_TIME);

    //=== 필수 속성 ===//

    private final Map<String, Object> attributes;
    private final Set<GrantedAuthority> authorities;

    protected CustomOAuth2User(Map<String, Object> attributes) {
        for (String s : REQUIRED_ATTRIBUTES) {
            if (!attributes.containsKey(s))
                throw new IllegalArgumentException("인증 정보 생성에 실패했습니다.(해당 속성이 없습니다:" + s + ")" );
        }

        this.attributes = Map.copyOf(attributes);
        if (this.attributes.get("sub") == "TEST") {
            this.authorities = Set.of(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_TEST"));
        } else {
            this.authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    //=== 정적 팩토리 메서드 ===//

    public static CustomOAuth2User newUser(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes
    ) {
        try {
            return from(OAuth2Registration.valueOf(registrationId.toUpperCase())
                    .extractAuthInfo(userNameAttributeName, attributes));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("지원하지 않는 로그인 제공자입니다.(" + registrationId + ")");
        }
    }

    public static CustomOAuth2User from(Map<String, Object> attributes) {
        return new CustomOAuth2User(attributes);
    }

    //=== public 메서드 ===//

   @Override
    public String getName() {
        return getAttributes().get(SUBJECT).toString();
    }

    public Authentication getAuthentication() {
        return new OAuth2AuthenticationToken(this, getAuthorities(),
                getAttributes().get(REGISTRATION).toString());
    }

    public OAuth2Identifier getIdentifier() {
        return OAuth2Identifier.of(
                getAttributes().get(SUBJECT).toString(),
                getAttributes().get(REGISTRATION).toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomOAuth2User that = (CustomOAuth2User) o;
        return Objects.equals(getAttributes(), that.getAttributes()) && Objects.equals(getAuthorities(), that.getAuthorities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAttributes(), getAuthorities());
    }
}
