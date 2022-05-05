package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.exception.ResourceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Profile("!prod")
@RequiredArgsConstructor
@Service
@Slf4j
public class LocalImageService implements ImageService, InitializingBean {

    @Value("${file.upload.path:~}")
    private String LOCAL_FILE_PATH;

    private File BASE_PATH;

    private final ImageRepository imageRepository;

    // TODO: use dedicated exception
    @Override
    public Image upload(MultipartFile imageFile) {
        validateMultipartFile(imageFile);

        // TODO: getOriginalFilename(), handle edge cases
        File dstFile = new File(BASE_PATH, imageFile.getOriginalFilename());
        try {
            imageFile.transferTo(dstFile);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed.", e);
        }

        log.info("file saved. {}", dstFile.getAbsolutePath());
        return imageRepository.save(new Image(dstFile.getName()));
    }

    private void validateMultipartFile(MultipartFile imageFile) {
        if (imageFile.getOriginalFilename() == null) {
            throw new RuntimeException("파일 경로가 올바르지 않습니다.");
        }
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(imageFile.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("올바르지 않은 미디어 타입입니다.");
        }
        if (!mediaType.equals(MediaType.IMAGE_JPEG)) {
            throw new RuntimeException("로컬에서는 jpeg 포맷만 업로드 가능합니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 이미지(id: " + id + ")"));
    }

    @Override
    public Boolean deleteById(Long id) {
        imageRepository.deleteById(id);
        return true;
    }

    @Override
    public ImageFileDto getImage(String key) {
        return imageRepository.findByUrl(key).map(image -> {
            File file = new File(BASE_PATH, image.getUrl());
            try {
                // FIXME: 미디어 타입 파일에서 가져오기, 현재는 jpeg 로 고정
                return new ImageFileDto(new FileInputStream(file), MediaType.IMAGE_JPEG);
            } catch (Exception e) {
                log.error("Failed convert file to stream.", e);
                return new ImageFileDto(null, null);
            }
        }).orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 이미지(key: " + key + ")"));
    }

    @Override
    public void afterPropertiesSet() {
        BASE_PATH = new File(LOCAL_FILE_PATH);
        BASE_PATH.mkdirs();
        log.info("Local image upload location: {}", BASE_PATH.getAbsolutePath());
    }
}
