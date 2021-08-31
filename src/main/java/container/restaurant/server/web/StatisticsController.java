package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/total-container")
    public ResponseEntity<?> getFeedStatistics() {
        return ResponseEntity.ok(statisticsService.totalContainer());
    }

}
