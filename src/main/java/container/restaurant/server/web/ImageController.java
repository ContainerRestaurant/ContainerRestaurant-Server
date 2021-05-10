package container.restaurant.server.web;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {

    final private ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestBody MultipartFile image) throws IOException {
        Image imageInfo = imageService.upload(image);
        return ResponseEntity.ok(EntityModel.of(imageInfo)
                .add(linkTo(getController().upload(image)).withSelfRel())
                .add(linkTo(getController()).slash(imageInfo.getUrl()).withRel("image_url")));
    }

    @GetMapping(value = "{path}")
    public ResponseEntity<?> getImageFile() {

        return null;
    }

    private ImageController getController() {
        return methodOn(ImageController.class);
    }
}
