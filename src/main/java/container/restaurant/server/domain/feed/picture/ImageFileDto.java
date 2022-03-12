package container.restaurant.server.domain.feed.picture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.io.InputStream;

@RequiredArgsConstructor
@Getter
public class ImageFileDto {

    private final InputStream imageStream;

    private final MediaType imageType;

    public static ImageFileDto from(InputStream imageStream, String imageType) {
        if ("jpg".equals(imageType)) imageType = "jpeg";
        if (!imageType.startsWith("image/")) imageType = "image/" + imageType;

        return new ImageFileDto(imageStream, MediaType.valueOf(imageType));
    }

}
