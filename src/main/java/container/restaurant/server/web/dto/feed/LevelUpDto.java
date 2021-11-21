package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.user.ContainerLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LevelUpDto {
    private final Integer levelFeedCount;
    private final String to;

    public static LevelUpDto to(ContainerLevel to) {
        return new LevelUpDto(to.getNeedCount(), to.getTitle());
    }
}
