package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.scrap.FeedScrapService;
import container.restaurant.server.web.linker.UserScrapLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/scrap")
public class UserScrapController {

    private final FeedScrapService feedScrapService;

    private final UserScrapLinker userScrapLinker;

    @PostMapping("{feedId}")
    public ResponseEntity<?> scrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedScrapService.userScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(userScrapLinker.scrapFeed(feedId).withSelfRel())
                        .add(userScrapLinker.cancelScrapFeed(feedId).withRel("cancel-scrap"))
        );
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> cancelScrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedScrapService.userCancelScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(userScrapLinker.cancelScrapFeed(feedId).withSelfRel())
                        .add(userScrapLinker.scrapFeed(feedId).withRel("scrap"))
        );
    }

}
