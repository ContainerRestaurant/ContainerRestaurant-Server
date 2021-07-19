package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
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
            @PathVariable Long feedId, @LoginId Long loginId
    ) {
        reportService.reportFeed(loginId, feedId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("comment/{commentId}")
    public ResponseEntity<?> reportComment(
            @PathVariable Long commentId, @LoginId Long loginId
    ) {
        reportService.reportComment(loginId, commentId);
        return ResponseEntity.noContent().build();
    }
}
