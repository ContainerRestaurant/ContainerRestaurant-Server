package container.restaurant.server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private ObjectMapper mapper;

    private User myself;
    private User other;

    @BeforeEach
    public void beforeEach() {
        myself = userRepository.save(User.builder()
                .email("me@test.com")
                .profile("https://my.profile.path")
                .build());
        session.setAttribute("user", SessionUser.from(myself));
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
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 정보 조회")
    void testGetUserSelf() throws Exception {
        mvc.perform(
                get("/api/user/{id}", myself.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(myself.getEmail()))
                .andExpect(jsonPath("nickname").value(myself.getNickname()))
                .andExpect(jsonPath("profile").value(myself.getProfile()))
                .andExpect(jsonPath("level").value(myself.getLevel()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 정보 조회 실패 (404)")
    void testFailToGetInvalidUser() throws Exception {
        mvc.perform(
                get("/api/user/{id}", -1)
                        .session(session))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("errorType")
                                .value(ResourceNotFoundException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("존재하지 않는 사용자입니다.(id:-1)"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 닉네임, 프로필 업데이트")
    void testUpdateUser() throws Exception {
        String nickname = "this는nikname이라능a";
        String profile = "http://profile.path";
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname(nickname)
                .profile(profile)
                .build();

        mvc.perform(
                patch("/api/user/{id}", myself.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(myself.getEmail()))
                .andExpect(jsonPath("nickname").value(nickname))
                .andExpect(jsonPath("profile").value(profile))
                .andExpect(jsonPath("level").value(myself.getLevel()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 업데이트 실패 (400)")
    void testFailToUpdateUserBy400() throws Exception {
        String nickname = "this는nikname이라능!";
        String profile = "httpprofile.path";
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname(nickname)
                .profile(profile)
                .build();

        mvc.perform(
                patch("/api/user/{id}", myself.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("errorType")
                                .value(ConstraintViolationException.class.getSimpleName()))
                .andExpect(jsonPath("messages", Matchers.containsInAnyOrder(
                        "닉네임은 한글/영문/숫자/공백만 입력 가능하며, " +
                                    "1~10자의 한글이나 2~20자의 영문/숫자/공백만 입력 가능합니다.",
                                "프로필의 URL 형식이 잘못되었습니다."
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 업데이트 실패 (403)")
    void testFailToUpdateUserBy403() throws Exception {
        String nickname = "this는nikname이라능a";
        String profile = "http://profile.path";
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname(nickname)
                .profile(profile)
                .build();

        mvc.perform(
                patch("/api/user/{id}", other.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("errorType")
                                .value(FailedAuthorizationException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("해당 사용자의 정보를 수정할 수 없습니다.(id:" + other.getId() + ")"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 탈퇴")
    void testDeleteUser() throws Exception {
        mvc.perform(
                delete("/api/user/{id}", myself.getId())
                        .session(session))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(myself.getId())).isFalse();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("사용자 탈퇴 실패 (403)")
    void testFailToDeleteUser() throws Exception {
        mvc.perform(
                delete("/api/user/{id}", other.getId())
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errorType")
                        .value(FailedAuthorizationException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("해당 사용자의 정보를 수정할 수 없습니다.(id:" + other.getId() + ")"));

        assertThat(userRepository.existsById(other.getId())).isTrue();
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