package container.restaurant.server.utils;

import container.restaurant.server.domain.feed.picture.Image;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public final class ImageUtils {

    private static final UriComponentsBuilder DEFAULT_URL_BUILDER = UriComponentsBuilder
            .fromPath("http://localhost:8080").pathSegment("api", "image");

    private static UriComponentsBuilder imageUrlBuilder = DEFAULT_URL_BUILDER;

    private static void initUrlBuilder() {
        if (imageUrlBuilder != DEFAULT_URL_BUILDER ||
            !(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes))
            return;

        imageUrlBuilder = ServletUriComponentsBuilder
                .fromCurrentContextPath().pathSegment("api", "image");
    }

    public static String getFileServerUrl(String path) {
        initUrlBuilder();
        return imageUrlBuilder.cloneBuilder().path(path).build().toUriString();
    }

    public static String getUrlFromImage(Image image) {
        return image != null && image.getUrl() != null ?
                getFileServerUrl(image.getUrl()) : null;
    }

}
