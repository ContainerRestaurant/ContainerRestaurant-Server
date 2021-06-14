package container.restaurant.server.config.auth.dto;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {

    private Long id;
    private String email;

    public static SessionUser from(@Valid User user) {
        return new SessionUser(user.getId(), user.getEmail());
    }

    public static SessionUser from(UserDto.Info info) {
        return new SessionUser(info.getId(), info.getEmail());
    }
}
