package container.restaurant.server.config.auth;

import static java.util.Optional.ofNullable;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.constant.Header;
import container.restaurant.server.domain.user.UserService;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoginUserFilter implements Filter {

    private static final Long NO_LOGIN_USER_ID = 0L;

    private final HttpSession httpSession;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Long userId = ofNullable(request.getAttribute("login_user"))
                .filter(CustomOAuth2User.class::isInstance)
                .map(CustomOAuth2User.class::cast)
                .map(authUser -> userService.getUserIdFromIdentifier(authUser.getIdentifier()))
                .orElse(NO_LOGIN_USER_ID);

        httpSession.setAttribute("user_id", userId);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(Header.USER_ID, userId.toString());

        chain.doFilter(request, response);
    }
}
