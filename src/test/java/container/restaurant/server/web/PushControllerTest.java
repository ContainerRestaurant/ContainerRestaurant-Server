package container.restaurant.server.web;

import container.restaurant.server.domain.push.PushTokenService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PushControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    PushTokenService pushTokenService;

    @Test
    void registerClientPushToken() throws Exception {
        String testToken = "ASDFDWAQWERASDFZXCV1";

        mvc.perform(post("/api/push/{token}", testToken))
                .andExpect(status().isOk())
                .andDo(document("push-token-register"));
    }

    @Test
    void deleteClientPushToken() throws Exception {
        String testToken = "ASDFDWAQWERASDFZXCV2";
        Long id = pushTokenService.registerPushToken(testToken).getId();

        mvc.perform(delete("/api/push/{tokenId}", id))
                .andExpect(status().isNoContent())
                .andDo(document("push-token-delete"));
    }
}