package container.restaurant.server.web;

import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthListControllerTest extends BaseMvcControllerTest {

    @Test
    @DisplayName("auth 링크 테스트")
    void testAuthLinks() throws Exception {
        String testPath = "/auth/list";
        mvc.perform(get(testPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(containsString(testPath)))
                .andExpect(jsonPath("_links.kakao.href").value(containsString("kakao")))
                .andExpect(jsonPath("_links.google.href").value(containsString("google")))
                .andDo(document("auth-list",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("kakao").description("OAuth 2.0 을 이용해 카카오로 인증하는 링크"),
                                linkWithRel("google").description("OAuth 2.0 을 이용해 구글로 인증하는 링크")
                        )
                ));
    }

}