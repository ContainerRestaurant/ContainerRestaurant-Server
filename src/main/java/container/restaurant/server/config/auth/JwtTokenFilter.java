package container.restaurant.server.config.auth;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.utils.jwt.JwtLoginService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        CustomOAuth2User loginUser = jwtLoginService.parse(token);

        SecurityContextHolder.getContext().setAuthentication(loginUser.getAuthentication());
        filterChain.doFilter(request, response);
    }

}
