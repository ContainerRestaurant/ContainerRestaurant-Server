package container.restaurant.server.config.auth;

import static java.util.Optional.ofNullable;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.UserService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        return ofNullable(httpSession.getAttribute("user_id"))
                .orElse(ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .map(Authentication::getPrincipal)
                        .filter(CustomOAuth2User.class::isInstance)
                        .map(CustomOAuth2User.class::cast)
                        .map(authUser -> userService.getUserIdFromIdentifier(authUser.getIdentifier()))
                        .orElse(null));
    }
}
