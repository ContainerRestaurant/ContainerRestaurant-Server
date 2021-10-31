package container.restaurant.server.web;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageFileDto;
import container.restaurant.server.web.dto.image.ImageDto;
import container.restaurant.server.web.linker.ImageLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import container.restaurant.server.domain.feed.picture.ImageService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {

    final private ImageService imageService;

    final private ImageLinker imageLinker;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImageFile(@RequestBody MultipartFile image) throws IOException, URISyntaxException {
        Image imageInfo = imageService.upload(image);
        return ResponseEntity.ok(EntityModel.of(ImageDto.of(imageInfo))
                .add(imageLinker.uploadImage(image).withSelfRel())
                .add(imageLinker.getImage(imageInfo.getUrl()).withRel("image_url")));
    }

    @GetMapping(value = "{path}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String path) {
        final ImageFileDto imageDto = imageService.getImage(path);

        return ResponseEntity.ok()
                .contentType(imageDto.getImageType())
                .body(new InputStreamResource(imageDto.getImageStream()));
    }

}
