package container.restaurant.server.web;

import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RestaurantControllerTest extends BaseUserAndFeedControllerTest {

    @Test
    void findById() throws Exception {

        mvc.perform(get("/api/restaurant/{restaurantId}", restaurant.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("restaurant-get",
                        responseFields(
                                fieldWithPath("name").description("식당 이름"),
                                fieldWithPath("address").description("식당 주소"),
                                fieldWithPath("latitude").description("식당 위치 - 위도"),
                                fieldWithPath("longitude").description("식당 위치 - 경도"),
                                fieldWithPath("image_path").description("이미지 경로"),
                                fieldWithPath("feedCount").description("식당에 대한 피드 개수"),
                                fieldWithPath("difficultyAvg").description("식당의 난이도 평균"),
                                fieldWithPath("isContainerFriendly").description("용기 친화 식당 여부"),
                                fieldWithPath("isFavorite").description("로그인한 사용자의 식당 즐겨찾기 여부"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 목록")
                        ),
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("restaurant-vanish").description("식당 없어짐을 알리는 링크")
                        )
                ));

    }

    //TODO MySQL 이 아니라서 동작안하는데 방법을 고민해 보자
//    @Test
//    void findNearByRestaurants() throws Exception {
//
//        mvc.perform(get("/api/restaurant/{latitude}/{longitude}/{radius}",
//                0.0, 0.0, 0L))
//                .andExpect(status().isOk());
//    }

    @Test
    void updateVanish() throws Exception {

        mvc.perform(post("/api/restaurant/vanish/{restaurantId}", restaurant.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("restaurant-vanish"));
    }
}
