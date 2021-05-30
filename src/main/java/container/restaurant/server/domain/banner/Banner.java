package container.restaurant.server.domain.banner;

import container.restaurant.server.domain.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@Entity(name = "TB_BANNER")
public class Banner extends BaseEntity {
    private String title;
    private String bannerURL;
    private String contentURL;
    private String additionalURL;
}
