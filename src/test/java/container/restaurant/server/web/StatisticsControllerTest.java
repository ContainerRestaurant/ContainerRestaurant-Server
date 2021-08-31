package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StatisticsControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    StatisticsService statisticsService;

    @Test
    void getFeedStatistics() throws Exception {
        //given
        statisticsService.updateWritersStatistic();
        String path = "/api/statistics/total-container";

        //when
        ResultActions perform = mvc.perform(get(path));

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document("statistics-total-container",
                        responseFields(
                                subsectionWithPath("latestWriters").description("최근에 피드를 작성한 작성자 100인의 리스트"),
                                subsectionWithPath("topWriters").description("가장 많은 피드를 작성한 작성자 10인의 리스트"),
                                fieldWithPath("writerCount").description("현재까지 피드를 작성한 작성자 인원"),
                                fieldWithPath("feedCount").description("현자까지 작성된 전체 피드 개수")
                        )));
    }

}