package container.restaurant.server.web;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/auth/list")
public class AuthListController {

    @GetMapping
    public ResponseEntity<?> auth() {
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build().add(
                        linkTo(AuthListController.class).withSelfRel(),
                        Link.of("/oauth2/authorization/google", "google"),
                        Link.of("/oauth2/authorization/kakao", "kakao")
                )
        );
    }

}
