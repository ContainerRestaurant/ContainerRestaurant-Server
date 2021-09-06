package container.restaurant.server.web.dto.statistics;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.user.ContainerLevel;
import container.restaurant.server.domain.user.User;
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

    public UserProfileDto(Long id, ContainerLevel level, String nickname, Image profile, LocalDateTime createdDate) {
        this(id, level.getTitle(), nickname, ImageService.getUrlFromImage(profile));
    }

    public static UserProfileDto from(User user) {
        return new UserProfileDto(user.getId(), user.getLevelTitle(),
                user.getNickname(), ImageService.getUrlFromImage(user.getProfile()));
    }
}
