package container.restaurant.server.web.dto.statistics;

import container.restaurant.server.domain.user.User;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Getter
public class LatestFeedUserInfoDto extends RepresentationModel<LatestFeedUserInfoDto> implements Serializable {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String profile;

    public static LatestFeedUserInfoDto from(User user) {
        return new LatestFeedUserInfoDto(user);
    }

    protected LatestFeedUserInfoDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
    }

}
