package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @PostMapping("feed/{feedId}")
    public ResponseEntity<?> reportFeed(
            @PathVariable Long feedId, @LoginUser SessionUser sessionUser
    ) {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("comment/{commentId}")
    public ResponseEntity<?> reportComment(
            @PathVariable Long commentId, @LoginUser SessionUser sessionUser
    ) {
        return ResponseEntity.notFound().build();
    }
}
