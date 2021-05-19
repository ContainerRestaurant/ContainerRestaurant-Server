package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.linker.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    private final StatisticsLinker statisticsLinker;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestFeedCreateUser() {
        return ResponseEntity.ok(CollectionModel.of(statisticsService.latestFeedCreateUsers()
                .stream().map(latestFeedUserInfoDto -> EntityModel.of(latestFeedUserInfoDto))
                .collect(Collectors.toList()))
                .add(statisticsLinker.getLatestFeedCreateUser().withSelfRel()));
    }

}
