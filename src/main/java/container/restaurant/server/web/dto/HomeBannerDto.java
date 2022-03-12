package container.restaurant.server.web.dto;

import container.restaurant.server.domain.home.banner.Banner;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeBannerDto {

    private final String bannerUrl;
    private final String contentUrl;
    private final String additionalUrl;

    public static HomeBannerDto of(Banner banner) {
        return new HomeBannerDto(banner.getBannerUrl(), banner.getContentUrl(), banner.getAdditionalUrl());
    }

}
