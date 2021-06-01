package container.restaurant.server.web;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.web.linker.ImageLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
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
        return ResponseEntity.ok(EntityModel.of(imageInfo)
                .add(imageLinker.uploadImage(image).withSelfRel())
                .add(imageLinker.getImage(imageInfo.getUrl()).withRel("image_url")));
    }

    @GetMapping(value = "{path}")
    public RedirectView getImageFile(@PathVariable String path) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(ImageService.getUrlFromPath(path));
        return redirectView;
    }
}
