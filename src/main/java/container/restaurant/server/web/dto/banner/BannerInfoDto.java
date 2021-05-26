package container.restaurant.server.web.dto.banner;

import container.restaurant.server.domain.banner.Banner;
import lombok.Getter;

@Getter
public class BannerInfoDto {
    private final String title;
    private final String bannerURL;
    private final String contentURL;
    private final String additionalURL;

    public static BannerInfoDto from(Banner banner){
        return new BannerInfoDto(banner);
    }

    protected BannerInfoDto(Banner banner){
        this.title = banner.getTitle();
        this.bannerURL = "http://localhost:8080/api/image/"+banner.getBannerURL();
        this.contentURL = banner.getContentURL() != null ?
                "http://localhost:8080/api/image/"+banner.getContentURL() : null;
        this.additionalURL = banner.getAdditionalURL() != null ?
                "http://localhost:8080/api/image/"+banner.getAdditionalURL() : null;
    }
}
