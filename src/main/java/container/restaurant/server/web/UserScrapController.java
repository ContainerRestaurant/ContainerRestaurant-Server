package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.scrap.FeedScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/scrap")
public class UserScrapController {

    private final FeedScrapService feedScrapService;

    @PostMapping("{feedId}")
    public ResponseEntity<?> scrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedScrapService.userScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> cancelScrapFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedScrapService.userCancelScrapFeed(sessionUser.getId(), feedId);
        return ResponseEntity.noContent().build();
    }

}
