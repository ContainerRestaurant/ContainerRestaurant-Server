package container.restaurant.server.web;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockHttpSession session;

    private User myself;
    private User other;

    @BeforeEach
    public void beforeEach() {
        myself = userRepository.save(User.builder()
                .email("me@test.com")
                .profile("https://my.profile.path")
                .build());
        session.setAttribute("user", new SessionUser(myself));
        other = userRepository.save(User.builder()
                .email("you@test.com")
                .profile("https://your.profile.path")
                .build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        session.clearAttributes();
    }

    @Test
    @DisplayName("사용자 정보 조회")
    void testGetUserSelf() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 (404)")
    void testFailToGetOtherUser() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 닉네임, 프로필 업데이트")
    void testUpdateUser() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 업데이트 실패 (400)")
    void testFailToUpdateUserBy400() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 업데이트 실패 (403)")
    void testFailToUpdateUserBy403() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 탈퇴")
    void testDeleteUser() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("사용자 탈퇴 실패 (403)")
    void testFailToDeleteUser() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("닉네임 중복 됨")
    void testNicknameExists() throws Exception {
        // TODO
        assert false;
    }

    @Test
    @DisplayName("닉네임 중복 안됨")
    void testNicknameNonExists() throws Exception {
        // TODO
        assert false;
    }

}