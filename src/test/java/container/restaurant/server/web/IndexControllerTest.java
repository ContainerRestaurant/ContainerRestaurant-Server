package container.restaurant.server.web;

import container.restaurant.server.web.base.BaseUserControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class IndexControllerTest extends BaseUserControllerTest {

    @Test
    @DisplayName("비로그인 index 링크 테스트")
    void testGuestIndexLinks() throws Exception {
        String testPath = "/";

        mvc.perform(get(testPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.login.href").exists())
                .andDo(document("index-guest",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("feed-list").description("전체 피드 리스트 링크 (페이징)"),
                                linkWithRel("feed-recommend").description("추천 피드 리스트 링크"),
                                linkWithRel("feed-create").description("피드를 생성하기 위한 링크"),
                                linkWithRel("top-users").description("누적 피드가 최근 30일간 제일 많은 10명에 대한 리스트 링크"),
                                linkWithRel("recent-users").description("최근에 피드를 작성한 사용자 리스트 링크"),
                                linkWithRel("restaurant-near").description("주위 식당을 조회하기 위한 링크"),
                                linkWithRel("banner-list").description("배너 리스트 링크"),
                                linkWithRel("login").description("인증을 위한 링크 리스트")
                        )
        ));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 index 링크 테스트")
    void testAuthIndexLinks() throws Exception {
        String testPath = "/";

        mvc.perform(
                get(testPath)
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.my-info").exists())
                .andExpect(jsonPath("_links.logout.href").exists())
                .andDo(document("index-user",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("feed-list").description("전체 피드 리스트 링크 (페이징)"),
                                linkWithRel("feed-recommend").description("추천 피드 리스트 링크"),
                                linkWithRel("feed-create").description("피드를 생성하기 위한 링크"),
                                linkWithRel("top-users").description("누적 피드가 최근 30일간 제일 많은 10명에 대한 리스트 링크"),
                                linkWithRel("recent-users").description("최근에 피드를 작성한 사용자 리스트 링크"),
                                linkWithRel("restaurant-near").description("주위 식당을 조회하기 위한 링크"),
                                linkWithRel("banner-list").description("배너 리스트 링크"),
                                linkWithRel("my-info").description("사용자 상세 정보 링크"),
                                linkWithRel("logout").description("현재 로그인을 로그아웃 하기 위한 링크")
                        ),
                        responseFields(
                                fieldWithPath("loginId").description("로그인한 사용자 ID 비로그인인 경우 null"),
                                fieldWithPath("myContainer").description("로그인한 사용자가 작성한 용기내 피드 개수"),
                                fieldWithPath("totalContainer").description("어제까지 작성된 전체 용기내 피드 개수"),
                                fieldWithPath("myLevel").description("로그인한 사용자의 레벨"),
                                fieldWithPath("phrase").description("메인 문구"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 명세")
                        )
                ));
    }

}