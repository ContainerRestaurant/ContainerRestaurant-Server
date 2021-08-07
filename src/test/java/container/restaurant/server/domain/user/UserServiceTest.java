package container.restaurant.server.domain.user;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.process.oauth.OAuthAgentService;
import container.restaurant.server.utils.jwt.JwtLoginService;
import container.restaurant.server.web.dto.user.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static container.restaurant.server.domain.user.OAuth2Registration.KAKAO;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService 단위 테스트")
public class UserServiceTest extends BaseMockTest {

    @InjectMocks UserService userService;

    @Mock UserRepository userRepository;
    @Mock OAuthAgentService oAuthAgentService;
    @Mock JwtLoginService jwtLoginService;

    @Test
    @DisplayName("토큰 요청 DTO 로 토큰 생성 - 새 유저 생성")
    void 토큰_요청_DTO_로_토큰_생성__새_유저_생성() {
        //given KAKAO 인증을 사용하는 토큰 요청 DTO, 유저 Principal, 인증 식별 VO, 생성될 유저 ID, 생성될 토큰이 주어졌을 때
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(KAKAO, "[KAKAO_ACCESS_TOKEN]");
        CustomOAuth2User auth2User = mock(CustomOAuth2User.class);
        OAuth2Identifier identifier = new OAuth2Identifier("[KAKAO_ID]", KAKAO);
        long newUserId = 1L;
        String newToken = "[CREATED_JWT]";

        when(oAuthAgentService.getAuthUser(dto)).thenReturn(auth2User);
        when(auth2User.getIdentifier()).thenReturn(identifier);
        when(userRepository.findByIdentifier(identifier)).thenReturn(empty());
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = spy((User) invocation.getArgument(0));
            when(user.getId()).thenReturn(newUserId);
            return user;
        });
        when(jwtLoginService.tokenize(auth2User)).thenReturn(newToken);

        //when 인증토큰을 요청하면
        UserDto.Token result = userService.newToken(dto);

        //then Null 이 아닌 DTO 가 반환됨 / 생성된 유저 ID 가 포함됨 / 생성된 토큰이 포함됨
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(newUserId);
        assertThat(result.getToken()).isEqualTo(newToken);
    }

    @Test
    @DisplayName("토큰 요청 DTO 로 토큰 생성 - 존재하는 유저")
    void 토큰_요청_DTO_로_토큰_생성__존재하는_유저() {
        //given KAKAO 인증을 사용하는 토큰 요청 DTO, 유저 Principal, 인증 식별 VO, 저장된 유저, 생성될 토큰이 주어졌을 때
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(KAKAO, "[KAKAO_ACCESS_TOKEN]");
        CustomOAuth2User auth2User = mock(CustomOAuth2User.class);
        OAuth2Identifier identifier = new OAuth2Identifier("[KAKAO_ID]", KAKAO);
        String newToken = "[CREATED_JWT]";
        User user = mock(User.class);

        when(oAuthAgentService.getAuthUser(dto)).thenReturn(auth2User);
        when(auth2User.getIdentifier()).thenReturn(identifier);
        when(userRepository.findByIdentifier(identifier)).thenReturn(of(user));
        when(user.getId()).thenReturn(1L);
        when(jwtLoginService.tokenize(auth2User)).thenReturn(newToken);

        //when 인증토큰을 요청하면
        UserDto.Token result = userService.newToken(dto);

        //then Null 이 아닌 DTO 가 반환됨 / 생성된 유저 ID 가 포함됨 / 생성된 토큰이 포함됨
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getToken()).isEqualTo(newToken);
    }

    @Test
    @DisplayName("토큰 요청 DTO 로 토큰 생성 - 잘못된 액세스 토큰")
    void 토큰_요청_DTO_로_토큰_생성__잘못된_액세스_토큰() {
        //given KAKAO 인증을 사용하는 토큰 요청 DTO, 유저 Principal, 인증 식별 VO, 생성될 유저 ID, 생성될 토큰이 주어졌을 때
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(KAKAO, "[KAKAO_ACCESS_TOKEN]");
        RuntimeException ex = mock(RuntimeException.class);

        when(oAuthAgentService.getAuthUser(dto)).thenThrow(ex);

        //expect 새 인증토큰을 요청하면 발생한 예외가 전이됨
        assertThatThrownBy(() -> userService.newToken(dto))
                .isEqualTo(ex);
    }

}
