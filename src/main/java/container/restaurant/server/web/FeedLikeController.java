package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.feed.like.FeedLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/like/feed/")
public class FeedLikeController {

    private final FeedLikeService feedLikeService;

    @PostMapping("{feedId}")
    public ResponseEntity<?> userLikeFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedLikeService.userLikeFeed(sessionUser.getId(), feedId);
        return ResponseEntity.noContent().build();
    }

}
