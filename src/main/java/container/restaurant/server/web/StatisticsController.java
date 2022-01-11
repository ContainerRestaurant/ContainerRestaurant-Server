package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/total-container")
    public ResponseEntity<?> getFeedStatistics() {
        return ResponseEntity.ok(statisticsService.totalStatistics());
    }

    @Secured("ROLE_TEST")
    @PostMapping("/daily-update")
    public ResponseEntity<?> dailyUpdate() {
        statisticsService.dailyUpdate();
        return ResponseEntity.noContent().build();
    }
}
