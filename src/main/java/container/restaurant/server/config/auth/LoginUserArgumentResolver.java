package container.restaurant.server.config.auth;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginId.class) != null;
        boolean isUserClass = Long.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        return ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.getPrincipal() instanceof CustomOAuth2User)
                .map(auth -> userService.getUserIdFromIdentifier(
                        ((CustomOAuth2User) auth.getPrincipal()).getIdentifier()))
                .orElseGet(() -> ofNullable(httpSession.getAttribute("userId"))
                        .map(o -> Long.valueOf(o.toString()))
                        .orElse(null)
                );
    }

}
