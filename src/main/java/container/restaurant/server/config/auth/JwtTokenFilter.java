package container.restaurant.server.config.auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.exception.UnauthorizedException;
import container.restaurant.server.utils.jwt.JwtLoginService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtLoginService jwtLoginService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            request.setAttribute("login_user", null);

            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        try {
            CustomOAuth2User loginUser = jwtLoginService.parse(token);
            SecurityContextHolder.getContext().setAuthentication(loginUser.getAuthentication());

            request.setAttribute("login_user", loginUser);
        } catch (UnauthorizedException e) {
            response.sendError(401, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}
