package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.user.scrap.FeedScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
