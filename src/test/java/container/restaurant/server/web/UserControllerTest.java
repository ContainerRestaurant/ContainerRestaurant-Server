package container.restaurant.server.web;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.user.OAuth2Registration;
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
    @DisplayName("?????? ?????? ??????")
    void ??????_??????_??????() throws Exception {
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

        //when ????????? ????????????
        ResultActions perform = mvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)));

        //then 1 ????????? ????????? dto ??? oAuthAgentService.getAuthUser() ??? ??????
        verify(oAuthAgentService).getAuthUser(captor.capture());
        assertThat(captor.getValue()).isEqualTo(dto);

        //then 2 ?????? ????????? ??????
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value(expectedToken))
                .andExpect(jsonPath("id").value(myself.getId()))
                .andDo(document("user-new-token",
                        responseFields(
                                fieldWithPath("token").description("????????? ?????? ??????"),
                                fieldWithPath("id").description("????????? ?????? ???????????? ???????????? ????????? ?????? ID"))));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
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
                                linkWithRel("self").description("??? ????????? ??????"),
                                linkWithRel("feeds").description("??? ???????????? ????????? ?????? ?????????"),
                                linkWithRel("patch").description("??? ???????????? ?????? ???????????? ??????," +
                                        "???????????? ???????????? ?????? ????????????."),
                                linkWithRel("delete").description("??? ???????????? ?????? ?????? ??????"),
                                linkWithRel("nickname-exists").description("????????? ?????? ?????? ??????, " +
                                        "??????????????? ????????????, {nickname}??? ???????????? ????????? ????????????."),
                                linkWithRel("scraps").description("??? ???????????? ???????????? ?????? ?????????"),
                                linkWithRel("restaurant-favorite").description("??? ???????????? ??????????????? ?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").description("??? ???????????? ????????? ID"),
                                fieldWithPath("email").description("??? ???????????? ????????? ??????"),
                                fieldWithPath("nickname").description("??? ???????????? ?????????"),
                                fieldWithPath("profile").description("??? ???????????? ????????? ??????"),
                                fieldWithPath("levelTitle").description("??? ???????????? ?????? ?????????"),
                                fieldWithPath("feedCount").description("??? ???????????? ?????? ??????"),
                                fieldWithPath("scrapCount").description("??? ???????????? ???????????? ?????? ??????"),
                                fieldWithPath("bookmarkedCount").description("??? ???????????? ???????????? ?????? ??????"),
                                subsectionWithPath("_links").description("??? ???????????? ?????? ????????? ?????? ?????????")
                        )));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??? ?????????")
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
    @DisplayName("????????? ?????? ?????? ?????? (404)")
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
                                .value("???????????? ?????? ??????????????????.(id:-1)"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("????????? ?????????, ????????? ????????????")
    void testUpdateUser() throws Exception {
        String nickname = "this???nikname?????????a";
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
                                fieldWithPath("nickname").description("(Optional) ????????? ?????????"),
                                fieldWithPath("profileId").description("(Optional) ????????? ????????? ?????? ?????????"),
                                fieldWithPath("pushToken").description("(Optional) ????????? ?????? ?????? ?????????")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("????????? ???????????? ?????? (400)")
    void testFailToUpdateUserBy400() throws Exception {
        String nickname = "this???nikname?????????!";
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
                                fieldWithPath("errorType").description("????????? ????????? ??????"),
                                fieldWithPath("messages").description("????????? ?????? ?????? ?????????, " +
                                        "1??? ????????? ???????????? ????????? ??? ??????.")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("????????? ???????????? ?????? (403)")
    void testFailToUpdateUserBy403() throws Exception {
        String nickname = "this???nikname?????????a";
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
                                .value("?????? ???????????? ????????? ????????? ??? ????????????.(id:" + other.getId() + ")"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("????????? ??????")
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
    @DisplayName("????????? ?????? ?????? (403)")
    void testFailToDeleteUser() throws Exception {
        mvc.perform(
                delete("/api/user/{id}", other.getId())
                        .session(myselfSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errorType")
                        .value(FailedAuthorizationException.class.getSimpleName()))
                .andExpect(
                        jsonPath("messages[0]")
                                .value("?????? ???????????? ????????? ????????? ??? ????????????.(id:" + other.getId() + ")"));

        assertThat(userRepository.existsById(other.getId())).isTrue();
    }

    @Test
    @DisplayName("????????? ?????? ???")
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
                                parameterWithName("nickname").description("?????? ????????? ????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("nickname").description("?????? ????????? ????????? ?????????"),
                                fieldWithPath("exists").description("?????? ?????? ?????? - true: ?????? ??? / false: ?????? ?????? ??????"),
                                subsectionWithPath("_links").ignored()
                        )));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void testNicknameNonExists() throws Exception {
        String nickname = "???????????????";

        mvc.perform(
                get("/api/user/nickname/exists")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").value(nickname))
                .andExpect(jsonPath("exists").value(false))
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????? ?????? ??????")
    void testInvalidNicknameExists() throws Exception {
        mvc.perform(
                get("/api/user/nickname/exists")
                        .param("nickname", "this???nikname?????????!"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("errorType")
                                .value(ConstraintViolationException.class.getSimpleName()))
                .andExpect(jsonPath("messages[0]")
                        .value("???????????? ??????/??????/??????/????????? ?????? ????????????, " +
                                        "1~10?????? ???????????? 2~20?????? ??????/??????/????????? ?????? ???????????????."));
    }

    @Test
    @DisplayName("?????? ????????? ??????")
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