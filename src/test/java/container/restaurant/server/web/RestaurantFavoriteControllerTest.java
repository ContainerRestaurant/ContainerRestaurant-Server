package container.restaurant.server.web;

import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteRepository;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RestaurantFavoriteControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    RestaurantFavoriteService restaurantFavoriteService;

    @Autowired
    RestaurantFavoriteRepository restaurantFavoriteRepository;

    @Override
    @AfterEach
    public void afterEach() {
        restaurantFavoriteRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser(roles = "USER")
    void userFavoriteRestaurant() throws Exception {

        mvc.perform(post("/api/favorite/restaurant/{restaurantId}",restaurant.getId())
                    .session(myselfSession))
                .andExpect(status().isOk())
                .andDo(document("restaurant-favorite",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("cancel-favorite").description("본 즐겨찾기를 취소하는 링크")
                        )
                ));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCancelFavoriteRestaurant() throws Exception {
        restaurantFavoriteService.userFavoriteRestaurant(myself.getId(), restaurant.getId());

        mvc.perform(delete("/api/favorite/restaurant/{restaurantId}",restaurant.getId())
                    .session(myselfSession))
                .andExpect(status().isOk())
                .andDo(document("restaurant-favorite-cancel",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("favorite").description("다시 즐겨찾기하는 링크")
                        )
                ));
    }

    @Test
    void findAllByUser() throws Exception {
        restaurantFavoriteService.userFavoriteRestaurant(myself.getId(), restaurant.getId());

        mvc.perform(get("/api/favorite/restaurant")
                    .session(myselfSession))
                .andExpect(status().isOk())
                .andDo(document("user-restaurant-favorite"));
    }

}