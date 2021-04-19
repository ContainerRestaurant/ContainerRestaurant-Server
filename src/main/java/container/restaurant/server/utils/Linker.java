package container.restaurant.server.utils;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.AuthListController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Component
public class Linker {

    private final HttpSession httpSession;

    public Link getAuthLink() {
        if (isAuthenticated())
            return Link.of("/logout").withRel("logout");
        return linkTo(AuthListController.class).withRel("auth");
    }

    private Boolean isAuthenticated() {
        return getUser() != null;
    }

    private SessionUser getUser() {
        return (SessionUser) httpSession.getAttribute("user");
    }
}
