package container.restaurant.server.utils;

import container.restaurant.server.domain.feed.picture.Image;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public final class ImageUtils {

    private static final UriComponentsBuilder imageUrlBuilder = ServletUriComponentsBuilder
            .fromCurrentContextPath().pathSegment("api", "image");

    public static String getFileServerUrl(String path) {
        return imageUrlBuilder.path(path).build().toUriString();
    }

    public static String getUrlFromImage(Image image) {
        return image != null && image.getUrl() != null ?
                getFileServerUrl(image.getUrl()) : null;
    }

}
