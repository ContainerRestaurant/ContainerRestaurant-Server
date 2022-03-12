package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.user.ContainerLevel;
import lombok.Getter;

import static container.restaurant.server.domain.user.ContainerLevel.getLevel;

@Getter
public class LevelUpDto {
    private final Integer levelFeedCount;
    private final String from;
    private final String to;

    private LevelUpDto(ContainerLevel to) {
        this.levelFeedCount = to.getNeedCount();
        this.to = to.getTitle();

        ContainerLevel levelFrom = getLevel(to.getLevel() - 1);
        this.from = levelFrom != null ? levelFrom.getTitle() : null;
    }

    public static LevelUpDto to(ContainerLevel to) {
        return new LevelUpDto(to);
    }
}
