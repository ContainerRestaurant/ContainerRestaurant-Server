package container.restaurant.server.web.dto.user;

import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
public class UserUpdateDto {

    @NicknameConstraint
    private String nickname;

    @URL(message = "프로필의 URL 형식이 잘못되었습니다.")
    private String profile;

    private PushToken pushToken;

    @Builder
    protected UserUpdateDto(String nickname, String profile, PushToken pushToken) {
        this.nickname = nickname;
        this.profile = profile;
        this.pushToken = pushToken;
    }

    public void updateUser(User user) {
        if (nickname != null)
            user.setNickname(nickname);
        if (profile != null)
            user.setProfile(profile);
        if (pushToken != null)
            user.setPushToken(pushToken);
    }

}
