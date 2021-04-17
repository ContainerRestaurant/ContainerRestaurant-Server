package container.restaurant.server.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class IndexControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("index 링크 테스트")
    void testIndexLinks() throws Exception {
        String testPath = "/";

        mvc.perform(get(testPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.auth.href").exists())
                .andDo(document("index",
                links(
                        linkWithRel("self").description("본 응답의 링크"),
                        linkWithRel("auth").description("인증을 위한 링크 리스트")
                )
        ));
    }

}