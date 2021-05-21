package container.restaurant.server.domain.feed.picture;


import com.google.gson.JsonObject;
import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.utils.MultipartUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;
    private final MultipartUtility multipartUtility;

    public Image upload(MultipartFile imageFile) throws IOException {
        multipartUtility.init();
        multipartUtility.addFilePart("image", imageFile);

        JsonObject pathJob = (JsonObject) multipartUtility.finish();

        String path = pathJob.get("path").getAsString();
        return imageRepository.save(new Image(path));
    }

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 이미지입니다.(id: " + id + ")"));
    }
}
