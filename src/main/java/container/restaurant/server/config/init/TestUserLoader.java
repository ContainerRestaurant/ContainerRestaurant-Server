package container.restaurant.server.config.init;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.utils.jwt.JwtLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static container.restaurant.server.domain.user.OAuth2Registration.TEST;

@Component
@RequiredArgsConstructor
public class TestUserLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JwtLoginService jwtLoginService;

    @Override
    public void run(String... args) {
        CustomOAuth2User tester = CustomOAuth2User.from(TEST.extractAuthInfo(null, null));

        userRepository.findByIdentifier(tester.getIdentifier())
                .orElseGet(() -> userRepository.save(User.builder().identifier(tester.getIdentifier()).build()));

        System.out.println("### TESTER TOKEN: " + jwtLoginService.tokenize(tester));
    }
}
