package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.OAuth2Identifier;
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
    protected ImageRepository imageRepository;

    @Autowired
    protected MockHttpSession myselfSession;

    protected User myself;
    protected User other;
    protected Image image;
    protected String myselfAuthId = "myselfAuthId";
    protected String otherAuthId = "otherAuthId";

    @BeforeEach
    public void beforeEach() {

        image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        myself = userRepository.save(User.builder()
                .identifier(OAuth2Identifier.of(myselfAuthId, OAuth2Registration.KAKAO))
                .email("me@test.com")
                .profile(image)
                .nickname("나의닉네임")
                .build());

        myselfSession.setAttribute("userId", myself.getId());
        other = userRepository.save(User.builder()
                .identifier(OAuth2Identifier.of(otherAuthId, OAuth2Registration.KAKAO))
                .email("you@test.com")
                .profile(image)
                .nickname("남의닉네임")
                .build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        imageRepository.deleteAll();
        myselfSession.clearAttributes();
    }
}
