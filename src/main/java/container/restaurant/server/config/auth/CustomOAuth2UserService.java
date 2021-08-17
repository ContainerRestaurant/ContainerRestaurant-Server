package container.restaurant.server.config.auth;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final HttpSession httpSession;

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = getUserFromRequest(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        CustomOAuth2User authUser = CustomOAuth2User.newUser(
                registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Long userId = userService.getUserIdFromIdentifier(authUser.getIdentifier());

        httpSession.setAttribute("userId", userId);

        return authUser;
    }

    private OAuth2User getUserFromRequest(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        return delegate.loadUser(userRequest);
    }

}
