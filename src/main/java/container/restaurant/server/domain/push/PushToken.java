package container.restaurant.server.domain.push;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@Entity(name = "TB_PUSH_TOKEN")
public class PushToken extends BaseCreatedTimeEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @Builder
    protected PushToken(String token) {
        this.token = token;
    }
}
