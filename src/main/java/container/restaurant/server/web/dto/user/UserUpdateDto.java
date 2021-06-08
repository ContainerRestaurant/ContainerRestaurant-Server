package container.restaurant.server.web.dto.user;

import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateDto {

    @NicknameConstraint
    private String nickname;

    private Long profileId;

    private PushToken pushToken;

    @Builder
    protected UserUpdateDto(String nickname, Long profileId, PushToken pushToken) {
        this.nickname = nickname;
        this.profileId = profileId;
        this.pushToken = pushToken;
    }
}
