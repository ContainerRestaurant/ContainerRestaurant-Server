package container.restaurant.server.config.auth;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.utils.jwt.JwtLoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@DisplayName("JWT 토큰 필터 단위 테스트")
class JwtTokenFilterTest extends BaseMockTest {

    @InjectMocks JwtTokenFilter jwtTokenFilter;

    @Mock JwtLoginService jwtLoginService;

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain filterChain;

    @Mock SecurityContext context;

    @BeforeEach
    void setSecurityContextHolder() {
        SecurityContextHolder.setContext(context);
    }

    @ParameterizedTest(name = "Authorization: {0}")
    @CsvSource({ ",", "no proper format"})
    @DisplayName("Authorization 헤더가 유효하지 않은 경우")
    void Authorization_헤더가_유효하지_않은_경우(String authorization) throws Exception {
        //given 유효하지 않은 Authorization 헤더가 주어졌을 때
        when(request.getHeader(AUTHORIZATION)).thenReturn(authorization);

        //when 필터를 통과했을 때
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        //then Authentication 설정되지 않음 / 다음 필터 체인을 발생시킴
        verify(context, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("정상 동작")
    void 정상_동작() throws Exception {
        //given 유효한 Authorization 헤더가 주어졌을 때
        String token = "[TEST_TOKEN]";
        when(request.getHeader(AUTHORIZATION)).thenReturn(makeBearer(token));

        CustomOAuth2User mockedUser = mock(CustomOAuth2User.class);
        when(jwtLoginService.parse(token)).thenReturn(mockedUser);

        Authentication mockedAuthentication = mock(Authentication.class);
        when(mockedUser.getAuthentication()).thenReturn(mockedAuthentication);

        //when 필터를 통과했을 때
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        //then Authentication 가 설정됨 / 다음 필터 체인을 발생시킴
        assert SecurityContextHolder.getContext() == context;
        verify(context).setAuthentication(mockedAuthentication);
        verify(filterChain).doFilter(request, response);
    }

    private String makeBearer(String token) {
        return String.join(" ", "Bearer", token);
    }
    
}