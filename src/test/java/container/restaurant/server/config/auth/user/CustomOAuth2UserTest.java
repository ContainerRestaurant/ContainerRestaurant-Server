package container.restaurant.server.config.auth.user;

import container.restaurant.server.domain.user.OAuth2Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;

import static container.restaurant.server.config.auth.user.CustomOAuth2User.*;
import static container.restaurant.server.domain.user.OAuth2Registration.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("CustomOAuth2User 단위 테스트")
class CustomOAuth2UserTest {

    @Test
    @DisplayName("인증된 유저 테스트")
    void 카카오로_인증된_유저_테스트() {
        //given
        String registrationId = "kakao";
        String userNameAttributeName = "id";

        String sub = "testId";
        String email = "testEmail";
        Map<String, Object> attrs = Map.of(
                "id", sub,
                "kakao_account", Map.of(
                        "email", email));
        
        //when
        CustomOAuth2User result = CustomOAuth2User.newUser(registrationId, userNameAttributeName, attrs);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAttributes())
                .containsEntry(SUBJECT, sub)
                .containsEntry(REGISTRATION, KAKAO.toString())
                .containsEntry(EMAIL, email)
                .containsKey(EXPIRATION_TIME)
                .containsKey(ISSUED_AT);
        assertThat(result.getAuthorities().size()).isEqualTo(1);
        assertThat(result.getAuthorities()).allMatch(a -> "ROLE_USER".equals(a.getAuthority()));
        assertThat(result.getName()).isEqualTo(sub);
        assertThat(result.getIdentifier()).isEqualTo(OAuth2Identifier.of(sub, registrationId));
    }

    @Test
    @DisplayName("정적 팩토리 메서드 테스트")
    void 정적_팩토리_메서드_테스트() {
        //given
        String sub = "testSUBJECT";
        String registrationId = "kakao";
        Map<String, Object> map = Map.of(
                SUBJECT, sub,
                EXPIRATION_TIME, "testEXPIRATION_TIME",
                ISSUED_AT, "testISSUED_AT",
                EMAIL, "testEMAIL",
                REGISTRATION, registrationId
        );

        //when
        CustomOAuth2User result = from(map);

        //then
        assertThat(result.getAttributes()).isEqualTo(map);
        assertThat(result.getAuthorities()).allMatch(a -> "ROLE_USER".equals(a.getAuthority()));
        assertThat(result.getName()).isEqualTo(sub);
        assertThat(result.getIdentifier()).isEqualTo(OAuth2Identifier.of(sub, registrationId));
    }

    @Test
    @DisplayName("Authentication 생성 테스트")
    void Authentication_생성_테스트() {
        //given
        CustomOAuth2User user = mock(CustomOAuth2User.class);
        doReturn(Set.of(new SimpleGrantedAuthority("ROLE_USER"))).when(user).getAuthorities();
        when(user.getAuthentication()).thenCallRealMethod();
        when(user.getAttributes()).thenReturn(Map.of(REGISTRATION, KAKAO));
        when(user.getName()).thenReturn("name");

        //when
        Authentication authentication = user.getAuthentication();

        //then
        assertThat(authentication.getAuthorities()).allMatch(a -> "ROLE_USER".equals(a.getAuthority()));
        assertThat(authentication.getName()).isEqualTo("name");
        assertThat(authentication.getPrincipal()).isEqualTo(user);
        assertThat(authentication.getCredentials()).isEqualTo("");
        assertThat(authentication.getDetails()).isEqualTo(null);
    }
    
}