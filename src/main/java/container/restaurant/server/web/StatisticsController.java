package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.linker.StatisticsLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    private final StatisticsLinker statisticsLinker;

    @GetMapping("/latest")
    public ResponseEntity<?> getRecentFeedUsers() {
        return ResponseEntity.ok(EntityModel.of(statisticsService.getRecentFeedUsers())
                .add(statisticsLinker.getRecentFeedUsers().withSelfRel()));
    }
}
