package container.restaurant.server.web;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.user.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static container.restaurant.server.domain.user.OAuth2Registration.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("UserController 단위 테스트")
class UserControllerUnitTest extends BaseMockTest {

    @InjectMocks UserController userController;

    @Mock UserService userService;

    @Test
    @DisplayName("인증 토큰 생성 테스트")
    void 인증_토큰_생성_테스트() {
        //given 스텁된 토큰 요청 DTO 와 토큰 DTO 가 주어졌을 때
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(KAKAO, "[KAKAO_ACCESS_TOKEN]");
        UserDto.Token token = mock(UserDto.Token.class);

        when(userService.newToken(dto)).thenReturn(token);

        //when 주어진 토큰 요청 DTO 로 토큰을 요청하면
        ResponseEntity<UserDto.Token> result = userController.tokenRequest(dto);

        //then status OK / 주어진 토큰 DTO 가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo(token);
    }

}