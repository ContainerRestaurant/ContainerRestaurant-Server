package container.restaurant.server.web.dto.feed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedCreateResultDto {

    private final Long feedId;
    private final LevelUpDto levelUp;

}
