package container.restaurant.server.web.dto.banner;

import container.restaurant.server.domain.home.banner.Banner;
import lombok.Getter;

@Getter
public class BannerInfoDto {
    private final String title;
    private final String bannerURL;
    private final String contentURL;
    private final String additionalURL;

    // TODO: 로직 확인하고 제거
    private static final String HOST = "http://ec2-52-78-66-184.ap-northeast-2.compute.amazonaws.com";

    public static BannerInfoDto from(Banner banner, String baseURL) {
        return new BannerInfoDto(banner, baseURL);
    }

    protected BannerInfoDto(Banner banner, String baseURL) {
        baseURL = HOST + baseURL;
        this.title = banner.getTitle();
        this.bannerURL = baseURL + banner.getBannerUrl();
        this.contentURL = banner.getContentUrl() != null ?
                baseURL + banner.getContentUrl() : null;
        this.additionalURL = banner.getAdditionalUrl() != null ?
                banner.getAdditionalUrl() : null;
    }
}
