package container.restaurant.server.domain.feed.picture;


import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    // TODO: use optional
    Image upload(MultipartFile imageFile);

    // TODO: use optional
    Image findById(Long id);

    Boolean deleteById(Long id);

    ImageFileDto getImage(String key);

}
