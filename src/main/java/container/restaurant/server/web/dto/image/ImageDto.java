package container.restaurant.server.web.dto.image;

import container.restaurant.server.domain.feed.picture.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ImageDto {
    private final Long id;
    private final String uri;

    public static ImageDto of(Image image) {
        return new ImageDto(image.getId(), image.getUrl());
    }
}
