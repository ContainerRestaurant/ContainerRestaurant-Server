package container.restaurant.server.web;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerTest extends BaseMvcControllerTest {

    @MockBean private ImageService imageService;

    @Test
    @WithMockUser(roles = "USER")
    void testUploadImageFile() throws Exception {
        Image testImage = spy(new Image("IMAGE_URI"));
        when(testImage.getId()).thenReturn(3L);
        when(imageService.upload(any())).thenReturn(testImage);

        mvc.perform(
                post("/api/image/upload")
                        .content("IMAGE_FILE".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andDo(document("image-upload",
                        requestHeaders(
                                headerWithName("Content-Type").description(MediaType.MULTIPART_FORM_DATA_VALUE + " ????????? ????????????.")
                        ),
                        responseFields(
                                fieldWithPath("id").description("????????? ??? ???????????? ?????? ID"),
                                fieldWithPath("uri").description("????????? ??? ???????????? ???????????? URI"),
                                subsectionWithPath("_links").description("????????? ???????????? ????????? ?????? ??????")
                        )
                ));
    }

}