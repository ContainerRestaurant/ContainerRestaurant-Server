package container.restaurant.server.web;

import container.restaurant.server.domain.home.banner.Banner;
import container.restaurant.server.domain.home.banner.BannerRepository;
import container.restaurant.server.domain.home.banner.BannerService;
import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BannerControllerTest extends BaseMvcControllerTest {

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    BannerService bannerService;

    @Test
    @DisplayName("배너 테스트")
    public void getBanners() throws Exception {
        for (int i = 0; i < 4; i++) {
            bannerRepository.save(Banner.builder()
                    .bannerUrl("bannerURL" + i)
                    .additionalUrl("additionalURL" + i)
                    .contentUrl("contentURL" + i)
                    .title("title" + i)
                    .build());
        }
        bannerService.putBanners();


        ResultActions perform = mvc.perform(
                get("/api/banners")
        );

        perform
                .andExpect(status().isOk())
                .andDo(document("banners"))
                .andDo(document("banner",
                        responseFields(beneathPath("_embedded.bannerInfoDtoList"),
                                fieldWithPath("title").description("배너 컨텐츠 이름"),
                                fieldWithPath("bannerURL").description("메인 화면에서 보여질 이미지 URL"),
                                fieldWithPath("contentURL").description("메인 화면에서 배너를 터치한 경우 보여질 이미지 URL"),
                                fieldWithPath("additionalURL").description("배너 이미지 외에 추가로 제공해야할 URL 이 있는 경우 포함되는 URL")
                        )));
    }

}