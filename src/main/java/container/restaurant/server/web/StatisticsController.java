package container.restaurant.server.web;

import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.web.dto.statistics.StatisticsUserDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.StatisticsLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsLinker statisticsLinker;
    private final UserLinker userLinker;
    private final FeedLinker feedLinker;

    @GetMapping("/total-container")
    public ResponseEntity<?> getFeedStatistics() {
        return ResponseEntity.ok(statisticsService.totalContainer());
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getRecentFeedUsers() {
        return ResponseEntity.ok(EntityModel.of(statisticsService.getRecentFeedUsers())
                .add(statisticsLinker.getRecentFeedUsers().withSelfRel()));
    }

    @GetMapping("/top")
    public ResponseEntity<?> getFeedCountTopUsers() {
        return ResponseEntity.ok(CollectionModel.of(statisticsService.getFeedCountTopUsers()
                .stream()
                .map(userInfoDto -> EntityModel.of(setLinks(userInfoDto)))
                .collect(Collectors.toList()))
                .add(statisticsLinker.getFeedCountTopUsers().withSelfRel()));
    }

    private StatisticsUserDto setLinks(StatisticsUserDto dto) {
        return dto.add(
                userLinker.getUserById(dto.getId()).withSelfRel(),
                feedLinker.selectUserFeed(dto.getId()).withRel("feeds")
        );
    }
}
