package container.restaurant.server.web.linker;

import container.restaurant.server.web.ImageController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ImageLinker {

    ImageController proxy =
            DummyInvocationUtils.methodOn(ImageController.class);

    public LinkBuilder uploadImage(MultipartFile image) throws IOException, URISyntaxException {
        return linkTo(proxy.uploadImageFile(image));
    }

    public LinkBuilder getImage(String path) throws URISyntaxException {
        return linkTo(proxy.getImageFile(path));
    }


}
