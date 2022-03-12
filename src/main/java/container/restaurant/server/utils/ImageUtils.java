package container.restaurant.server.utils;

import container.restaurant.server.domain.feed.picture.Image;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public final class ImageUtils {

    // TODO: 이미지 url 생성 방식 개선
    private static final UriComponentsBuilder DEFAULT_URL_BUILDER = UriComponentsBuilder
            .fromUriString("http://dev.hellozin.net").pathSegment("api", "image");

    private static UriComponentsBuilder imageUrlBuilder = DEFAULT_URL_BUILDER;

    public static String getFileServerUrl(String path) {
        return imageUrlBuilder.cloneBuilder().path(path).build().toUriString();
    }

    public static String getUrlFromImage(Image image) {
        return image != null && image.getUrl() != null ?
                getFileServerUrl(image.getUrl()) : null;
    }

}
