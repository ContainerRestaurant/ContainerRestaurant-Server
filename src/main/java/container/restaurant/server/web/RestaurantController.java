package container.restaurant.server.web;

import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.web.dto.restaurant.RestaurantDetailDto;
import container.restaurant.server.web.linker.RestaurantLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantLinker restaurantLinker;

    private final RestaurantService restaurantService;

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        RestaurantDetailDto dto = restaurantService.getRestaurantInfoById(id);
        return ResponseEntity.ok(EntityModel.of(dto)
                .add(restaurantLinker.findById(id).withSelfRel())
                .add(restaurantLinker.updateVanish(id).withRel("restaurant-vanish"))
        );
    }

    @GetMapping("{lat}/{lon}/{radius}")
    public ResponseEntity<?> findNearByRestaurants(
            @PathVariable("lat") Double lat,
            @PathVariable("lon") Double lon,
            @PathVariable("radius") Long radius) {
        /*
            사용자 정보를 PathVariable 형태로 받는 형태로 구현 추후 입력방식 변경 가능
         */
        if (lat == null || lon == null || radius == null)
            return ResponseEntity.badRequest().body("위도, 경도, 반경 값이 필요합니다.");

        return ResponseEntity.ok(CollectionModel.of(restaurantService.findNearByRestaurants(lat, lon, radius)
                .stream().map(restaurant -> EntityModel.of(restaurant)
                        .add(restaurantLinker.findById(restaurant.getId()).withRel("restaurant-info"))
                        .add(restaurantLinker.updateVanish(restaurant.getId()).withRel("restaurant-vanish"))
                ).collect(Collectors.toList()))
                .add(restaurantLinker.findNearByRestaurants(lat, lon, radius).withSelfRel()));
    }

    @PostMapping("vanish/{id}")
    public ResponseEntity<?> updateVanish(@PathVariable("id") Long id) {
        restaurantService.restaurantVanish(id);
        return ResponseEntity.noContent().build();
    }

}