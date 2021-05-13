package container.restaurant.server.web.base;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

public abstract class BaseUserControllerTest extends BaseMvcControllerTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MockHttpSession myselfSession;

    protected User myself;
    protected User other;

    @BeforeEach
    public void beforeEach() {
        myself = User.builder()
                .email("me@test.com")
                .profile("https://my.profile.path")
                .build();
        myself.setNickname("테스트닉네임");
        myself = userRepository.save(myself);

        myselfSession.setAttribute("user", SessionUser.from(myself));
        other = userRepository.save(User.builder()
                .email("you@test.com")
                .profile("https://your.profile.path")
                .build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.delete(myself);
        userRepository.delete(other);
        myselfSession.clearAttributes();
    }
}
