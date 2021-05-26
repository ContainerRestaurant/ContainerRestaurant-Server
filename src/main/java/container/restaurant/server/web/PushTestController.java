package container.restaurant.server.web;

import container.restaurant.server.utils.FirebaseCloudMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/push")
public class PushTestController {

    final private FirebaseCloudMessageUtils fcmu;

    @GetMapping(value = "/{token}")
    public ResponseEntity<?> getImageFile(@PathVariable("token") String token) throws IOException {
        fcmu.sendMessage(token, "용기낸 식당", "Push 알림 테스트 입니다.");
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(linkTo(PushTestController.class).withSelfRel()));
    }
}
