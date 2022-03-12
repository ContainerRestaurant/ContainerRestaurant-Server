package container.restaurant.server.web;

import container.restaurant.server.domain.home.banner.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/banners")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<?> getBanners(){
        return ResponseEntity.ok(
                bannerService.getBanners()
        );
    }

}
