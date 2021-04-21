package container.restaurant.server.web;

import container.restaurant.server.utils.Linker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class IndexController {

    private final Linker linker;

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build().add(
                        linkTo(IndexController.class).withSelfRel(),
                        linker.getAuthLink()
                )
        );
    }

}
