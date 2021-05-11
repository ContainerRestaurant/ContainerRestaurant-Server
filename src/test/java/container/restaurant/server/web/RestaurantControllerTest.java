package container.restaurant.server.web;

import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RestaurantControllerTest extends BaseMvcControllerTest {
    private final String DEFAULT_PATH = "/api/restaurant/";

    @ParameterizedTest
    @DisplayName("식당 아이디로 조회")
    @CsvSource({
            "1, 용기낸 식당, 경기도 성남시 어딘가, 37.43924967392599, 127.12760127510042", // 태평역 5번 출구
            "2, 피자나라 치킨공주, 경기도 성남시 수정구, 37.43302666744512, 127.12918108322958", // 모란역 1번 출구
            "3, 두배마니, 경기도 성남시 저긴가, 37.43679000288809, 127.13189113423786", // 성남수정초
            "4, 롯데리아, 경기도 시흥시 태평동, 37.44139426827272, 127.13277707448933", // 수정구 우체국
    })
    void testFindById(Long id, String name, String addr, float lat, float lon) throws Exception {
        mvc.perform(
                get(DEFAULT_PATH + "{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("address").value(addr))
                .andExpect(jsonPath("latitude").value(lat))
                .andExpect(jsonPath("longitude").value(lon))
                .andExpect(jsonPath("image_path").isNotEmpty())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.image-url.href").exists());
    }
}