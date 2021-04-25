package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public ResponseEntity<?> index(@LoginUser SessionUser sessionUser) {
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(linksOf(sessionUser)));
    }

    private List<Link> linksOf(SessionUser sessionUser) {
        List<Link> links = new ArrayList<>();
        links.add(linkTo(IndexController.class).withSelfRel());
        if (sessionUser == null) {
            links.add(linkTo(AuthListController.class).withRel("auth-list"));
        } else {
            links.add(linkTo(methodOn(UserController.class).getUserById(sessionUser.getId(), sessionUser))
                    .withRel("my-info"));
            links.add(Link.of("/logout").withRel("logout"));
        }
        return links;
    }

}
