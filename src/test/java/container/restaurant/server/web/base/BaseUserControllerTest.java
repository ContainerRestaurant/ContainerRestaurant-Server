package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.user.AuthProvider;
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
                .authId(myselfAuthId)
                .authProvider(AuthProvider.KAKAO)
                .email("me@test.com")
                .profile(image)
                .nickname("나의닉네임")
                .build());

        myselfSession.setAttribute("userId", myself.getId());
        other = userRepository.save(User.builder()
                .authId(otherAuthId)
                .authProvider(AuthProvider.KAKAO)
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
