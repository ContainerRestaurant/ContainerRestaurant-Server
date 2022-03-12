package container.restaurant.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Embeddable
public class OAuth2Identifier {

    @Column(nullable = false, name = "auth_id")
    private String subject;

    @Column(nullable = false, name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private OAuth2Registration registration;

    public static OAuth2Identifier of(String subject, OAuth2Registration registration) {
        return new OAuth2Identifier(subject, registration);
    }

    public static OAuth2Identifier of(String subject, String registrationId) {
        return new OAuth2Identifier(subject, OAuth2Registration.valueOf(registrationId.toUpperCase(Locale.ROOT)));
    }

}
