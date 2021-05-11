package container.restaurant.server.web.dto.user;

import container.restaurant.server.domain.user.User;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class UserInfoDto extends RepresentationModel<UserInfoDto> {

    private final Long id;

    private final String email;
    private final String nickname;
    private final String profile;
    private final Integer level;
    private final Integer feedCount;
    private final Integer scrapCount;
    private final Integer bookmarkedCount;

    public static UserInfoDto from(User user) {
        return new UserInfoDto(user);
    }

    protected UserInfoDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
        this.level = user.getLevel();
        this.feedCount = user.getFeedCount();
        this.scrapCount = user.getScrapCount();
        this.bookmarkedCount = user.getBookmarkedCount();
    }

}
