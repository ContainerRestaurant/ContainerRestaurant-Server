package container.restaurant.server.config.auth.dto;

import container.restaurant.server.domain.user.User;

public class SessionUser {

    private final String email;
    private final String profile;

    public SessionUser(User user) {
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }
}
