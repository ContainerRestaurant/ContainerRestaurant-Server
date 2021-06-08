package container.restaurant.server.config.auth.dto;

import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@NoArgsConstructor
public class SessionUser implements Serializable {

    private Long id;
    private String email;

    protected SessionUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    public static SessionUser from(@Valid User user) {
        return new SessionUser(user);
    }
}
