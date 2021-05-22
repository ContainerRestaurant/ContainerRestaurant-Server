package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("feed/{feedId}")
    public ResponseEntity<?> reportFeed(
            @PathVariable Long feedId, @LoginUser SessionUser sessionUser
    ) {
        reportService.reportFeed(sessionUser.getId(), feedId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("comment/{commentId}")
    public ResponseEntity<?> reportComment(
            @PathVariable Long commentId, @LoginUser SessionUser sessionUser
    ) {
        reportService.reportComment(sessionUser.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
