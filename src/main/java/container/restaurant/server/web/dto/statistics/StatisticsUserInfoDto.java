package container.restaurant.server.web.dto.statistics;

import container.restaurant.server.domain.user.User;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Getter
public class StatisticsUserInfoDto extends RepresentationModel<StatisticsUserInfoDto> implements Serializable {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String profile;

    public static StatisticsUserInfoDto from(User user) {
        return new StatisticsUserInfoDto(user);
    }

    protected StatisticsUserInfoDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
    }

}
