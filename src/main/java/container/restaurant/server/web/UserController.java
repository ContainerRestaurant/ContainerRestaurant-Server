package container.restaurant.server.web;

import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserInfoDto dto = userService.findById(id);
        return ResponseEntity.ok().body(
                EntityModel.of(dto)
                        .add(linkTo(UserController.class).slash(id).withSelfRel()));
    }

}
