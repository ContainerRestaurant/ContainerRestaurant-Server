package container.restaurant.server.web;

import container.restaurant.server.domain.image.Image;
import container.restaurant.server.domain.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {

    final private ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestBody MultipartFile image) {
        Image imageInfo = imageService.upload(image);
        return ResponseEntity.ok(EntityModel.of(imageInfo)
                .add(linkTo(getController().upload(image)).withSelfRel()));
    }

    private ImageController getController() {
        return methodOn(ImageController.class);
    }
}
