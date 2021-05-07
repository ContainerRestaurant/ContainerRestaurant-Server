package container.restaurant.server.domain.image;


import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.utils.fileUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final FeedRepository feedRepository;
    private final ImageRepository imageRepository;

    public Image upload(MultipartFile image) {
        fileUpload fileUpload = new fileUpload();
        fileUpload.upload(image);

        return null;
    }
}
