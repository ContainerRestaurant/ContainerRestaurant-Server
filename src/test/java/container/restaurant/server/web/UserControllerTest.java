package container.restaurant.server.web;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.process.oauth.OAuthAgentService;
import container.restaurant.server.utils.jwt.JwtLoginService;
import container.restaurant.server.web.base.BaseUserControllerTest;
import container.restaurant.server.web.dto.user.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    OAuthAgentService oAuthAgentService;

    @MockBean
    JwtLoginService jwtLoginService;

    @Autowired
    HttpSession httpSession;

    @Test
    @DisplayName("인증 토큰 생성")
    void 인증_토큰_생성() throws Exception {
        //given
        OAuth2Registration reg = OAuth2Registration.KAKAO;
        String accessToken = "[TEST_ACCESS_TOKEN]";
        UserDto.ToRequestToken dto = new UserDto.ToRequestToken(reg, accessToken);

        String expectedToken = "[NEW_AUTH_TOKEN]";
        ArgumentCaptor<UserDto.ToRequestToken> captor = ArgumentCaptor.forClass(UserDto.ToRequestToken.class);
        CustomOAuth2User authUser = mock(CustomOAuth2User.class);

        when(oAuthAgentService.getAuthUser(any())).thenReturn(authUser);
        when(authUser.getIdentifier()).thenReturn(myself.getIdentifier());
        when(jwtLoginService.tokenize(authUser)).thenReturn(expectedToken);

        //when 요청을 수행하면
        ResultActions perform = mvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)));

        //then 1 요청에 사용한 dto 로 oAuthAgentService.getAuthUser() 를 호출
        verify(oAuthAgentService).getAuthUser(captor.capture());
        assertThat(captor.getValue()).isEqualTo(dto);

        //then 2 아래 명세를 따름
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value(expectedToken))
                .andExpect(jsonPath("id").value(myself.getId()))
                .andExpect(jsonPath("isNicknameNull").value(false))
                .andDo(document("user-new-token",
                        responseFields(
                                fieldWithPath("token").description("생성된 인증 토큰"),
                                fieldWithPath("id").description("생성된 인증 토큰으로 식별되는 유저의 식별 ID"),
                                fieldWithPath("isNicknameNull").description("로그인한 유저의 닉네임 null 여부"))));
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
                .andExpect(jsonPath("levelTitle").value(myself.getLevelTitle()))
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
                                fieldWithPath("levelTitle").description("본 사용자의 레벨 타이틀"),
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
                .andExpect(jsonPath("levelTitle").value(myself.getLevelTitle()))
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
    @WithMockUser(roles = "USER")
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
                .andExpect(jsonPath("levelTitle").value(myself.getLevelTitle()))
                .andExpect(jsonPath("feedCount").value(myself.getFeedCount()))
                .andExpect(jsonPath("scrapCount").value(myself.getScrapCount()))
                .andExpect(jsonPath("bookmarkedCount").value(myself.getBookmarkedCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists())
                .andExpect(jsonPath("_links.nickname-exists.href").exists())
                .andDo(document("patch-user",
                        requestFields(
                                fieldWithPath("nickname").description("(Optional) 변경할 닉네임"),
                                fieldWithPath("profileId").description("(Optional) 변경할 프로필 사진 식별자"),
                                fieldWithPath("pushToken").description("(Optional) 변경할 푸시 토큰 아이디")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
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
    @WithMockUser(roles = "USER")
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
    @WithMockUser(roles = "USER")
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
    @WithMockUser(roles = "USER")
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
    @WithMockUser(roles = "USER")
    @DisplayName("푸시 토큰 제거")
    void testUnregisterPushToken() throws Exception {
        myself.setPushToken(new PushToken("test token"));

        mvc.perform(
                delete("/api/user/{id}/push/token", myself.getId())
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andDo(document("unregister-user-push-token"));

        User myselfFromRepository = userRepository.findById(myself.getId()).orElseThrow();
        assertThat(myselfFromRepository.getPushToken()).isNull();
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
                .andExpect(jsonPath("levelTitle").value(myself.getLevelTitle()))
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