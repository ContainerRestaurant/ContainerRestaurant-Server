package container.restaurant.server.web;

import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurant/")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        RestaurantInfoDto restaurantInfoDto = restaurantService.getRestaurantInfoById(id);
        return ResponseEntity.ok(EntityModel.of(restaurantInfoDto)
                .add(linkTo(getController().findById(id)).withSelfRel())
                .add(linkTo(ImageController.class).slash(restaurantInfoDto.getImage_path()).withRel("image-url"))
                .add(linkTo(getController().updateVanish(id)).withRel("restaurant-vanish"))
        );
    }

    @GetMapping("/{lat}/{lon}/{radius}")
    public ResponseEntity<CollectionModel<?>> findNearByRestaurants(
            @PathVariable("lat") double lat,
            @PathVariable("lon") double lon,
            @PathVariable("radius") long radius) {
        /*
            사용자 정보를 PathVariable 형태로 받는 형태로 구현 추후 입력방식 변경 가능
         */

        return ResponseEntity.ok(new CollectionModel<>(restaurantService.findNearByRestaurants(lat, lon, radius)
                .stream().map(restaurant -> EntityModel.of(restaurant)
                        .add(linkTo(getController().findById(restaurant.getId())).withRel("restaurant-info"))
                        .add(linkTo(getController().updateVanish(restaurant.getId())).withRel("restaurant-vanish"))
                        .add(linkTo(ImageController.class).slash(restaurant.getImage_path()).withRel("image-url"))
                ).collect(Collectors.toList()))
                .add(linkTo(getController().findNearByRestaurants(lat, lon, radius)).withSelfRel()));
    }

    @GetMapping("search/{name}")
    public ResponseEntity<CollectionModel<?>> searchRestaurantName(@PathVariable("name") String name) {
        return ResponseEntity.ok(new CollectionModel<>(restaurantService.searchRestaurantName(name)
                .stream().map(restaurant -> EntityModel.of(restaurant)
                        .add(linkTo(getController().findById(restaurant.getId())).withRel("restaurant-info"))
                ).collect(Collectors.toList()))
                .add(linkTo(getController().searchRestaurantName(name)).withSelfRel()));
    }

    @PostMapping("vanish/{id}")
    public ResponseEntity<?> updateVanish(@PathVariable("id") Long id) {
        restaurantService.updateVanish(id);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(linkTo(getController().updateVanish(id)).withSelfRel()));
    }

    private RestaurantController getController() {
        return methodOn(RestaurantController.class);
    }

}