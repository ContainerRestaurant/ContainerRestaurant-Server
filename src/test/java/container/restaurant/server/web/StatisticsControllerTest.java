package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StatisticsControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    StatisticsService statisticsService;

    @Test
    void getRecentFeedUsers() throws Exception {
        statisticsService.addRecentUser(myself);
        statisticsService.addRecentUser(other);

        mvc.perform(get("/api/statistics/latest"))
                .andExpect(status().isOk())
                .andDo(document("statistics-latest",
                        responseFields(
                                subsectionWithPath("statisticsUserDto").description("최근에 피드를 작성한 유저 목록"),
                                fieldWithPath("todayFeedCount").description("금일 작성된 피드 개수"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 목록")
                        )
                ));
    }

    @Test
    void getTopFeedUsers() throws Exception {
        statisticsService.updateFeedCountTopUsers();

        mvc.perform(get("/api/statistics/top"))
                .andExpect(status().isOk())
                .andDo(document("statistics-top"));
    }

}