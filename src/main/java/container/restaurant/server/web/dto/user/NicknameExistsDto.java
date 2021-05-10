package container.restaurant.server.web.dto.user;

import lombok.Getter;

@Getter
public class NicknameExistsDto {

    private String nickname;
    private Boolean exists;

    protected NicknameExistsDto(String nickname, Boolean exists) {
        this.nickname = nickname;
        this.exists = exists;
    }

    public static NicknameExistsDto of(String nickname, Boolean exists) {
        return new NicknameExistsDto(nickname, exists);
    }

}
