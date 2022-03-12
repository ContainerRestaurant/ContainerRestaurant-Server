package container.restaurant.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static container.restaurant.server.config.auth.user.CustomOAuth2User.*;
import static org.assertj.core.api.Assertions.assertThat;

class OAuth2RegistrationTest {

    @Test
    @DisplayName("카카오 인증 정보 추출 테스트 - 이메일 있음")
    void 카카오_인증_정보_추출_테스트__이메일_있음() {
        //given 실제 카카오 API 응답 예제가 주어진 경우
        int id = 10101010;
        String email = "test@test.com";
        Map<String, Object> attr = Map.of(
                "id", id,
                "connected_at", "2021-08-17T05:25:09Z",
                "kakao_account", Map.of(
                        "email", email,
                        "has_email", true,
                        "email_needs_agreement", true
                )
        );

        //when
        Map<String, Object> result = OAuth2Registration.KAKAO.extractAuthInfo("id", attr);

        //then
        assertThat(result.get(SUBJECT)).isEqualTo(id);
        assertThat(result.get(REGISTRATION)).isEqualTo("KAKAO");
        assertThat(result.get(EMAIL)).isEqualTo(email);
    }

    @Test
    @DisplayName("카카오 인증 정보 추출 테스트 - 이메일 없음")
    void 카카오_인증_정보_추출_테스트__없음() {
        //given 실제 카카오 API 응답 예제가 주어진 경우
        int id = 10101010;
        Map<String, Object> attr = Map.of(
                "id", id,
                "connected_at", "2021-08-17T05:25:09Z",
                "kakao_account", Map.of(
                        "has_email", true,
                        "email_needs_agreement", true
                )
        );

        //when
        Map<String, Object> result = OAuth2Registration.KAKAO.extractAuthInfo("id", attr);

        //then
        assertThat(result.get(SUBJECT)).isEqualTo(id);
        assertThat(result.get(REGISTRATION)).isEqualTo("KAKAO");
        assertThat(result.get(EMAIL)).isEqualTo("");
    }

}