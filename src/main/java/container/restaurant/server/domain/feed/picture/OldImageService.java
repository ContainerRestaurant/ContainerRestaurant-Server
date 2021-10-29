package container.restaurant.server.domain.feed.picture;

import com.google.gson.JsonObject;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.utils.MultipartUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RequiredArgsConstructor
public class OldImageService implements ImageService {

    private final ImageRepository imageRepository;
    private final MultipartUtility multipartUtility;

    public Image upload(MultipartFile imageFile) {
        try {
            multipartUtility.init();
            multipartUtility.addFilePart("image", imageFile);

            JsonObject pathJob = (JsonObject) multipartUtility.finish();

            String path = pathJob.get("path").getAsString();
            return imageRepository.save(new Image(path));
        } catch (IOException e) {
            throw new IllegalStateException("failed to save a image", e);
        }
    }

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return Optional.ofNullable(id)
                .map(imageRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 이미지입니다.(id: " + id + ")"))
                .orElse(null);
    }

    @Override
    public Boolean deleteById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getImageStream(String key) {
        throw new UnsupportedOperationException();
    }
}
