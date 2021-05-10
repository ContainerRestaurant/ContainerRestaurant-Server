package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.feed.like.FeedLikeService;
import container.restaurant.server.web.linker.FeedLikeLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/like/feed/")
public class FeedLikeController {

    private final FeedLikeService feedLikeService;

    private final FeedLikeLinker feedLikeLinker;

    @PostMapping("{feedId}")
    public ResponseEntity<?> userLikeFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedLikeService.userLikeFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(feedLikeLinker.userLikeFeed(feedId).withSelfRel())
                        .add(feedLikeLinker.userCancelLikeFeed(feedId).withRel("cancel-like"))
        );
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> userCancelLikeFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedLikeService.userCancelLikeFeed(sessionUser.getId(), feedId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(feedLikeLinker.userLikeFeed(feedId).withSelfRel())
                        .add(feedLikeLinker.userCancelLikeFeed(feedId).withRel("like"))
        );
    }

}
