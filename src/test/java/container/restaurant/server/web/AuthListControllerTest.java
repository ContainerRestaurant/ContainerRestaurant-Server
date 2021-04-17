package container.restaurant.server.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AuthListControllerTest {

    @Autowired
    private MockMvc mvc;

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