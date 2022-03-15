package container.restaurant.server.web;

import container.restaurant.server.domain.push.PushTokenService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PushControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    PushTokenService pushTokenService;

    @Test
    @WithMockUser(roles = "USER")
    void registerClientPushToken() throws Exception {
        String testToken = "ASDFDWAQWERASDFZXCV1";

        mvc.perform(post("/api/push/token/{token}", testToken))
                .andExpect(status().isOk())
                .andDo(document("push-token-register"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteClientPushToken() throws Exception {
        String testToken = "ASDFDWAQWERASDFZXCV2";
        Long id = pushTokenService.registerPushToken(testToken).getId();

        mvc.perform(delete("/api/push/token/{tokenId}", id))
                .andExpect(status().isNoContent())
                .andDo(document("push-token-delete"));
    }
}