package container.restaurant.server.web.dto;

import container.restaurant.server.domain.home.banner.Banner;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeBannerDto {

    private final Long bannerId;
    private final String bannerUrl;

    public static HomeBannerDto of(Banner banner) {
        return new HomeBannerDto(banner.getId(), banner.getBannerURL());
    }

}
