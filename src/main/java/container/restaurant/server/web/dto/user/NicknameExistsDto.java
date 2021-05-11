package container.restaurant.server.web.dto.user;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class NicknameExistsDto extends RepresentationModel<NicknameExistsDto> {

    private final String nickname;
    private final Boolean exists;

    protected NicknameExistsDto(String nickname, Boolean exists) {
        this.nickname = nickname;
        this.exists = exists;
    }

    public static NicknameExistsDto of(String nickname, Boolean exists) {
        return new NicknameExistsDto(nickname, exists);
    }

}
