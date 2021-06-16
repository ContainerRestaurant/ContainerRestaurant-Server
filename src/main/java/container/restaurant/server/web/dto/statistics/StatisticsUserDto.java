package container.restaurant.server.web.dto.statistics;

import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Getter
public class StatisticsUserDto extends RepresentationModel<StatisticsUserDto> implements Serializable {

    private final Long id;
    private final String levelTitle;
    private final String nickname;
    private final String profile;

    public static StatisticsUserDto from(User user) {
        return new StatisticsUserDto(user);
    }

    protected StatisticsUserDto(User user) {
        this.id = user.getId();
        this.levelTitle = user.getLevelTitle();
        this.nickname = user.getNickname();
        this.profile = ImageService.getUrlFromImage(user.getProfile());
    }

}
