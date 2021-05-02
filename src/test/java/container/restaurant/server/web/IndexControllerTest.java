package container.restaurant.server.web;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class IndexControllerTest extends BaseMvcControllerTest {

    @Test
    @DisplayName("비로그인 index 링크 테스트")
    void testGuestIndexLinks() throws Exception {
        String testPath = "/";

        mvc.perform(get(testPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.auth-list.href").exists())
                .andDo(document("index-guest",
                links(
                        linkWithRel("self").description("본 응답의 링크"),
                        linkWithRel("auth-list").description("인증을 위한 링크 리스트")
                )
        ));
    }

    @Test
    @DisplayName("로그인 index 링크 테스트")
    void testAuthIndexLinks() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.from(User.builder()
                .email("index@test.com")
                .profile("https://my.profile.path")
                .build()));
        String testPath = "/";

        mvc.perform(
                get(testPath)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.my-info").exists())
                .andExpect(jsonPath("_links.logout.href").exists())
                .andDo(document("index-user",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("my-info").description("현 사용자의 상세 정보 링크"),
                                linkWithRel("logout").description("현재 로그인을 로그아웃 하기 위한 링크")
                        )
                ));
    }

}