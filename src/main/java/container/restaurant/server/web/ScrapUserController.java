package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.scrap.ScrapFeedService;
import container.restaurant.server.web.linker.ScrapFeedLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/scrap")
public class ScrapUserController {

    private final ScrapFeedService scrapFeedService;

    private final ScrapFeedLinker scrapFeedLinker;

    @PostMapping("{feedId}")
    public ResponseEntity<?> scrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        scrapFeedService.userScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(scrapFeedLinker.scrapFeed(feedId).withSelfRel())
                        .add(scrapFeedLinker.cancelScrapFeed(feedId).withRel("cancel-scrap"))
        );
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> cancelScrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        scrapFeedService.userCancelScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(scrapFeedLinker.cancelScrapFeed(feedId).withSelfRel())
                        .add(scrapFeedLinker.scrapFeed(feedId).withRel("scrap"))
        );
    }

}
