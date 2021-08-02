package container.restaurant.server.web.linker;

import container.restaurant.server.web.ReportController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ReportLinker {

    ReportController proxy =
            DummyInvocationUtils.methodOn(ReportController.class);

    public LinkBuilder reportFeed(Long feedId) {
        return linkTo(proxy.reportFeed(feedId, -1L));
    }

    public LinkBuilder reportComment(Long commentId) {
        return linkTo(proxy.reportFeed(commentId, -1L));
    }


}
