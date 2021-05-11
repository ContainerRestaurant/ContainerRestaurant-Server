package container.restaurant.server.domain.feed.picture;


import com.google.gson.JsonObject;
import container.restaurant.server.utils.MultipartUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;

    public Image upload(MultipartFile imageFile) throws IOException {
        MultipartUtility mu = new MultipartUtility();
        mu.addFilePart("image", imageFile);

        JsonObject pathJob = (JsonObject) mu.finish();

        String path = pathJob.get("path").getAsString();
        return imageRepository.save(new Image(path));
    }
}
