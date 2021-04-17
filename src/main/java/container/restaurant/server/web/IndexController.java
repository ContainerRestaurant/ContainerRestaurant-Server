package container.restaurant.server.web;

import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build().add(
                        linkTo(IndexController.class).withSelfRel(),
                        linkTo(AuthListController.class).withRel("auth")
                )
        );
    }

}
