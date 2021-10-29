package container.restaurant.server.domain.feed.picture;


import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageService {

    Image upload(MultipartFile imageFile);

    Image findById(Long id);

    Boolean deleteById(Long id);

    InputStream getImageStream(String key);

}
