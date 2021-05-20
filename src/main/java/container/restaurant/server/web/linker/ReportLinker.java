package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.ReportController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ReportLinker {

    ReportController proxy =
            DummyInvocationUtils.methodOn(ReportController.class);

    SessionUser u =
            DummyInvocationUtils.methodOn(SessionUser.class);

    public LinkBuilder reportFeed(Long feedId) {
        return linkTo(proxy.reportFeed(feedId, u));
    }

    public LinkBuilder reportComment(Long commentId) {
        return linkTo(proxy.reportFeed(commentId, u));
    }


}
