package container.restaurant.server.web.dto.statistics;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.user.ContainerLevel;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.utils.ImageUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
public class UserProfileDto {
    private final Long id;
    private final String levelTitle;
    private final String nickname;
    private final String profile;

    public UserProfileDto(Long id, String levelTitle, String nickname, String profile) {
        this.id = id;
        this.levelTitle = levelTitle;
        this.nickname = nickname;
        this.profile = profile;
    }

    public String getProfile() {
        return ImageUtils.getFileServerUrl(profile);
    }

    public UserProfileDto(Long id, ContainerLevel level, String nickname, Image profile, LocalDateTime createdDate) {
        this(id, level.getTitle(), nickname, profile != null ? profile.getUrl() : null);
    }

    public static UserProfileDto from(User user) {
        return new UserProfileDto(user.getId(), user.getLevelTitle(),
                user.getNickname(), user.getProfile() != null ? user.getProfile().getUrl() : null);
    }
}
