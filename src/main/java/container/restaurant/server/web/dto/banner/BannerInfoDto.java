package container.restaurant.server.web.dto.banner;

import container.restaurant.server.domain.home.banner.Banner;
import lombok.Getter;

@Getter
public class BannerInfoDto {
    private final String title;
    private final String bannerURL;
    private final String contentURL;
    private final String additionalURL;

    public static BannerInfoDto from(Banner banner, String baseURL) {
        return new BannerInfoDto(banner, baseURL);
    }

    protected BannerInfoDto(Banner banner, String baseURL) {
        this.title = banner.getTitle();
        this.bannerURL = baseURL + banner.getBannerUrl();
        this.contentURL = banner.getContentUrl() != null ?
                baseURL + banner.getContentUrl() : null;
        this.additionalURL = banner.getAdditionalUrl() != null ?
                banner.getAdditionalUrl() : null;
    }
}
