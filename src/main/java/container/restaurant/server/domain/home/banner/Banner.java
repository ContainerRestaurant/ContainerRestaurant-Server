package container.restaurant.server.domain.home.banner;

import container.restaurant.server.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_BANNER")
public class Banner extends BaseEntity {
    private String title;
    private String bannerUrl;
    private String contentUrl;
    private String additionalUrl;
}
