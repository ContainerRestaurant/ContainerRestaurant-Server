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
        this.from = getLevel(to.getLevel() - 1).getTitle();
        this.to = to.getTitle();
    }

    public static LevelUpDto to(ContainerLevel to) {
        return new LevelUpDto(to);
    }
}
