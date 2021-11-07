package container.restaurant.server.web;

import container.restaurant.server.domain.home.banner.Banner;
import container.restaurant.server.domain.home.banner.BannerRepository;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.base.BaseUserControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class HomeControllerTest extends BaseUserControllerTest {

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    StatisticsService statisticsService;

    static boolean init = false;

    @BeforeEach
    @Transactional
    void addBanners() {
        if (!init) init();
    }

    void init() {
        init = true;

        bannerRepository.save(new Banner("title1", "banner1", "content1", "additional1"));
        bannerRepository.save(new Banner("title2", "banner2", "content2", "additional2"));
        bannerRepository.save(new Banner("title3", "banner3", "content3", "additional3"));

        statisticsService.addRecentUser(myself);
        statisticsService.addRecentUser(other);
    }

    @Test
    @DisplayName("홈 링크 테스트")
    void testAuthIndexLinks() throws Exception {
        String testPath = "/api/home";

        mvc.perform(
                get(testPath)
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(Matchers.containsString(testPath)))
                .andExpect(jsonPath("_links.my-info").exists())
                .andExpect(jsonPath("_links.logout.href").exists())
                .andDo(document("home",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("feed-list").description("전체 피드 리스트 링크 (페이징)"),
                                linkWithRel("feed-recommend").description("추천 피드 리스트 링크"),
                                linkWithRel("feed-create").description("피드를 생성하기 위한 링크"),
                                linkWithRel("restaurant-near").description("주위 식당을 조회하기 위한 링크"),
                                linkWithRel("banner-list").description("배너 리스트 링크"),
                                linkWithRel("my-info").description("사용자 상세 정보 링크"),
                                linkWithRel("total-container").description("모든 용기 통계 - 용기낸 경험 관련 통계 조회 링크"),
                                linkWithRel("logout").description("현재 로그인을 로그아웃 하기 위한 링크")
                        ),
                        responseFields(
                                fieldWithPath("loginId").description("로그인한 사용자 ID 비로그인인 경우 null"),
                                fieldWithPath("myContainer").description("로그인한 사용자가 작성한 용기내 피드 개수"),
                                fieldWithPath("totalContainer").description("어제까지 작성된 전체 용기내 피드 개수"),
                                fieldWithPath("myLevelTitle").description("로그인한 사용자의 레벨 타이틀"),
                                fieldWithPath("myProfile").description("로그인한 사용자의 프로필 이미지"),
                                fieldWithPath("phrase").description("메인 문구"),
                                subsectionWithPath("latestWriterProfile").description("최근 피드 작성자 3인의 정보"),
                                subsectionWithPath("banners").description("배너 Id 와 배너 이미지 url 목록"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 명세")
                        )
                ));
    }



}