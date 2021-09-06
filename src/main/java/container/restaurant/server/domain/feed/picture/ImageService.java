package container.restaurant.server.domain.feed.picture;


import com.google.gson.JsonObject;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.utils.MultipartUtility;
import container.restaurant.server.web.linker.ImageLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImageService {

    @Value("${server.image.base.url}")
    private String BASE_URL;
    private static final String DEFAULT_PATH = "/api/image/";

    private final ImageRepository imageRepository;
    private final MultipartUtility multipartUtility;
    private static final ImageLinker imageLinker = new ImageLinker();

    public Image upload(MultipartFile imageFile) throws IOException {
        multipartUtility.init();
        multipartUtility.addFilePart("image", imageFile);

        JsonObject pathJob = (JsonObject) multipartUtility.finish();

        String path = pathJob.get("path").getAsString();
        return imageRepository.save(new Image(path));
    }

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return Optional.ofNullable(id)
                .map(imageRepository::findById)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "존재하지 않는 이미지입니다.(id: " + id + ")"))
                .orElse(null);
    }

    public static String getUrlFromImage(Image image) {
        return Optional.ofNullable(image)
                .map(i -> getUrlFromPath(i.getUrl()))
                .orElse(null);
    }

    public String getFileServerUrl(String path) {
        return String.join("", BASE_URL, DEFAULT_PATH, path);
    }

    public static String getUrlFromPath(String path) {
        return path == null ? null : imageLinker.getImage(path).toString();
    }
}
