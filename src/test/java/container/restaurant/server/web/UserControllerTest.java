package container.restaurant.server.web;

import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.process.oauth.OAuthAgent;
import container.restaurant.server.process.oauth.OAuthAgentFactory;
import container.restaurant.server.web.base.BaseUserControllerTest;
import container.restaurant.server.web.dto.user.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest extends BaseUserControllerTest {

    @MockBean
    private OAuthAgentFactory oAuthAgentFactory;

    @Autowired
    HttpSession httpSession;

    @Test
    @DisplayName("토큰으로 로그인")
    void tokenLogin() throws Exception {
        //given-1 테스트용 액세스 토큰과 요청
        String testToken = "[ACCESS_TOKEN]";
        UserDto.TokenLogin dto = new UserDto.TokenLogin(testToken, myself.getAuthProvider());

        //given-2 OAuth Provider 로 부터 제공받은 사용자 정보 모킹 - myself 정보
        OAuthAgent agent = mock(OAuthAgent.class);
        when(oAuthAgentFactory.get(myself.getAuthProvider())).thenReturn(agent);
        when(agent.getAuthAttrFrom(testToken)).thenReturn(of(OAuthAttributes.builder()
                .provider(myself.getAuthProvider())
                .nickname("ProviderNickname")
                .authId(myself.getAuthId())
                .email(myself.getEmail())
                .build()));

        //when
        mvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(document("login-token",
                        requestFields(
                                fieldWithPath("provider").description("로그인할 사용자의 OAuth 제공자 +\n(KAKAO)"),
                                fieldWithPath("accessToken").description("로그인 인증할 액세스 토큰")
                        )
                ));

        //then myself 로 로그인 되어있다.
        assertThat(httpSession.getAttribute("user")).isNotNull();
        assertThat(((SessionUser) httpSession.getAttribute("user")).getId()).isEqualTo(myself.getId());
    }

    @Test
    @DisplayName("사용자 정보 조회")
    void testGetUserSelf() throws Exception {
        mvc.perform(
                get("/api/user/{id}", myself.getId())
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(myself.getId()))
                .andExpect(jsonPath("email").value(myself.getEmail()))
                .andExpect(jsonPath("nickname").value(myself.getNickname()))
                .andExpect(jsonPath("profile").value(containsString(image.getUrl())))
                .andExpect(jsonPath("level").value(myself.getLevel()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.feeds.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists())
                .andExpect(jsonPath("_links.nickname-exists.href").exists())
                .andExpect(jsonPath("_links.scraps.href").exists())
                .andDo(document("get-user",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("feeds").description("본 사용자가 작성한 피드 리스트"),
                                linkWithRel("patch").description("본 사용자의 정보 업데이트 링크," +
                                        "닉네임과 프로필만 수정 가능하다."),
                                linkWithRel("delete").description("본 사용자의 계정 탈퇴 링크"),
                                linkWithRel("nickname-exists").description("닉네임 중복 확인 링크, " +
                                        "템플릿으로 제공되어, {nickname}을 지정해야 사용이 가능하다."),
                                linkWithRel("scraps").description("본 사용자가 스크랩한 피드 리스트"),
                                linkWithRel("restaurant-favorite").description("본 사용자가 즐겨찾기한 식당 리스트")
                        ),
                        responseFields(
                                fieldWithPath("id").description("본 사용자의 구분자 ID"),
                                fieldWithPath("email").description("본 사용자의 이메일 주소"),
                                fieldWithPath("nickname").description("본 사용자의 닉네임"),
                                fieldWithPath("profile").description("본 사용자의 프로필 경로"),
                                fieldWithPath("level").description("본 사용자의 레벨"),
                                fieldWithPath("feedCount").description("본 사용자의 피드 개수"),
                                fieldWithPath("scrapCount").description("본 사용자의 스크랩한 피드 개수"),
                                fieldWithPath("bookmarkedCount").description("본 사용자의 즐겨찾는 식당 개수"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 리스트")
                        )));
    }

    @Test
    @DisplayName("사용자 정보 조회 - 타 사용자")
    void testGetUserOther() throws Exception {
        mvc.perform(
                get("/api/user/{id}", other.getId())
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(other.getId()))
                .andExpect(jsonPath("email").value(other.getEmail()))
                .andExpect(jsonPath("nickname").value(other.getNickname()))
                .andExpect(jsonPath("profile").value(containsString(image.getUrl())))
                .andExpect(jsonPath("level").value(other.getLevel()))
                .andExpect(jsonPath("feedCount").value(other.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(other.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(other.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.patch.href").doesNotExist())
                .andExpect(jsonPath("_links.delete.href").doesNotExist())
                .andExpect(jsonPath("_links.nickname-exists.href").doesNotExist());
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 (404)")
    void testFailToGetInvalidUser() throws Exception {
        mvc.perform(
                get("/api/user/{id}", -1)
                        .session(myselfSession))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("errorType")
                                .value(ResourceNotFoundException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("존재하지 않는 사용자입니다.(id:-1)"));
    }

    @Test
    @DisplayName("사용자 닉네임, 프로필 업데이트")
    void testUpdateUser() throws Exception {
        String nickname = "this는nikname이라능a";
        Image newImage = newImage();

        UserDto.Update userUpdateDto = UserDto.Update.builder()
                .nickname(nickname)
                .profileId(newImage.getId())
                .build();

        mvc.perform(
                patch("/api/user/{id}", myself.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(myself.getId()))
                .andExpect(jsonPath("email").value(myself.getEmail()))
                .andExpect(jsonPath("nickname").value(nickname))
                .andExpect(jsonPath("profile").value(containsString(newImage.getUrl())))
                .andExpect(jsonPath("level").value(myself.getLevel()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists())
                .andExpect(jsonPath("_links.nickname-exists.href").exists())
                .andDo(document("patch-user",
                        requestFields(
                                fieldWithPath("nickname").description("변경할 닉네임"),
                                fieldWithPath("profileId").description("변경할 프로필 사진 식별자"),
                                fieldWithPath("pushToken").description("변경할 푸시 토큰 아이디")
                        )));
    }

    @Test
    @DisplayName("사용자 업데이트 실패 (400)")
    void testFailToUpdateUserBy400() throws Exception {
        String nickname = "this는nikname이라능!";
        UserDto.Update userUpdateDto = UserDto.Update.builder()
                .nickname(nickname)
                .profileId(newImage().getId())
                .build();

        mvc.perform(
                patch("/api/user/{id}", myself.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(myselfSession))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("errorType")
                                .value(ConstraintViolationException.class.getSimpleName()))
                .andDo(document("error-example",
                        responseFields(
                                fieldWithPath("errorType").description("발생한 에러의 타입"),
                                fieldWithPath("messages").description("에러에 대한 상세 메시지, " +
                                        "1개 이상의 메시지가 발생할 수 있다.")
                        )));
    }

    @Test
    @DisplayName("사용자 업데이트 실패 (403)")
    void testFailToUpdateUserBy403() throws Exception {
        String nickname = "this는nikname이라능a";
        UserDto.Update userUpdateDto = UserDto.Update.builder()
                .nickname(nickname)
                .profileId(newImage().getId())
                .build();

        mvc.perform(
                patch("/api/user/{id}", other.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .session(myselfSession))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("errorType")
                                .value(FailedAuthorizationException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("해당 사용자의 정보를 수정할 수 없습니다.(id:" + other.getId() + ")"));
    }

    @Test
    @DisplayName("사용자 탈퇴")
    void testDeleteUser() throws Exception {
        mvc.perform(
                delete("/api/user/{id}", myself.getId())
                        .session(myselfSession))
                .andExpect(status().isNoContent())
                .andDo(document("delete-user"));

        assertThat(userRepository.existsById(myself.getId())).isFalse();
    }

    @Test
    @DisplayName("사용자 탈퇴 실패 (403)")
    void testFailToDeleteUser() throws Exception {
        mvc.perform(
                delete("/api/user/{id}", other.getId())
                        .session(myselfSession))
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
        mvc.perform(
                get("/api/user/nickname/exists")
                        .param("nickname",  myself.getNickname()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").value(myself.getNickname()))
                .andExpect(jsonPath("exists").value(true))
                .andExpect(jsonPath("_links.self.href").exists())
                .andDo(document("check-nickname-exists",
                        requestParameters(
                                parameterWithName("nickname").description("중복 검사를 진행할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("nickname").description("중복 검사를 진행한 닉네임"),
                                fieldWithPath("exists").description("중복 검사 결과 - true: 중복 됨 / false: 중복 되지 않음"),
                                subsectionWithPath("_links").ignored()
                        )));
    }

    @Test
    @DisplayName("닉네임 중복 안됨")
    void testNicknameNonExists() throws Exception {
        String nickname = "없는닉네임";

        mvc.perform(
                get("/api/user/nickname/exists")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").value(nickname))
                .andExpect(jsonPath("exists").value(false))
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("닉네임 중복 검사시 유효성 검사 실패")
    void testInvalidNicknameExists() throws Exception {
        mvc.perform(
                get("/api/user/nickname/exists")
                        .param("nickname", "this는nikname이라능!"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("errorType")
                                .value(ConstraintViolationException.class.getSimpleName()))
                .andExpect(jsonPath("messages[0]")
                        .value("닉네임은 한글/영문/숫자/공백만 입력 가능하며, " +
                                        "1~10자의 한글이나 2~20자의 영문/숫자/공백만 입력 가능합니다."));
    }

    @Test
    @DisplayName("현재 사용자 정보")
    void getUserId() throws Exception {
        mvc.perform(
                get("/api/user")
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(myself.getId()))
                .andExpect(jsonPath("email").value(myself.getEmail()))
                .andExpect(jsonPath("nickname").value(myself.getNickname()))
                .andExpect(jsonPath("profile").value(containsString(image.getUrl())))
                .andExpect(jsonPath("level").value(myself.getLevel()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.feeds.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists())
                .andExpect(jsonPath("_links.nickname-exists.href").exists())
                .andExpect(jsonPath("_links.scraps.href").exists())
                .andDo(document("my-info"));
    }

    private Image newImage() {
        return imageRepository.save(new Image("newImage"));
    }


}