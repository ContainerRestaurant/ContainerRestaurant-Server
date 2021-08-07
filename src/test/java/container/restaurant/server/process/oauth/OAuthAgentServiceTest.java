package container.restaurant.server.process.oauth;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.web.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static container.restaurant.server.domain.user.OAuth2Registration.KAKAO;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("OAuthAgentService 단위 테스트")
class OAuthAgentServiceTest extends BaseMockTest {

    @InjectMocks OAuthAgentService oAuthAgentService;

    @Mock OAuthAgentFactory agentFactory;
    @Mock OAuthAgent agent;

    @BeforeEach
    void 팩토리_스텁() {
        when(agentFactory.get(anyString())).thenReturn(agent);
        when(agentFactory.get(any(OAuth2Registration.class))).thenReturn(agent);
    }

    @Test
    @DisplayName("토큰 요청 DTO 로 유저 Principal 생성 테스트")
    void 토큰_요청_DTO_로_유저_Principal_생성_테스트() {
        //given 토큰 생성 DTO 가 주어지고 agent 의 동작이 스텁되어 있을 때
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(KAKAO, "[KAKAO_ACCESS_TOKEN]");

        CustomOAuth2User auth2User = mock(CustomOAuth2User.class);
        when(agent.getAuthUserFrom(dto.getAccessToken())).thenReturn(auth2User);

        //when 주어진 토큰 생성 DTO 로 Principal 을 생성하면
        CustomOAuth2User result = oAuthAgentService.getAuthUser(dto);

        //then 해당 Principal 은 agent 에 스텁된 Principal 과 동일함
        assertThat(result).isEqualTo(auth2User);
    }

}