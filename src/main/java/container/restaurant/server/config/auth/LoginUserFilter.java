package container.restaurant.server.config.auth;

import container.restaurant.server.constant.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class LoginUserFilter implements Filter {

    private final HttpSession httpSession;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ofNullable(httpSession.getAttribute("userId"))
                .ifPresent(object -> {
                    Long loginId = (Long) object;
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse.setHeader(Header.USER_ID, loginId.toString());
                });
        chain.doFilter(request, response);
    }
}
