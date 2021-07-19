package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteService;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import container.restaurant.server.web.linker.RestaurantLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/favorite/restaurant")
public class RestaurantFavoriteController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    private final RestaurantFavoriteLinker restaurantFavoriteLinker;
    private final RestaurantLinker restaurantLinker;

    @PostMapping("{restaurantId}")
    public ResponseEntity<?> userFavoriteRestaurant(
            @LoginId Long loginId, @PathVariable Long restaurantId
    ) {
        restaurantFavoriteService.userFavoriteRestaurant(loginId, restaurantId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(restaurantFavoriteLinker.userFavoriteRestaurant(restaurantId).withSelfRel())
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(restaurantId).withRel("cancel-favorite"))
        );
    }

    @DeleteMapping("{restaurantId}")
    public ResponseEntity<?> userCancelFavoriteRestaurant(
            @LoginId Long loginId, @PathVariable Long restaurantId
    ) {
        restaurantFavoriteService.userCancelFavoriteRestaurant(loginId, restaurantId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(restaurantId).withSelfRel())
                        .add(restaurantFavoriteLinker.userFavoriteRestaurant(restaurantId).withRel("favorite"))
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<?>> findAllByUser(@LoginId Long loginId) {
        return ResponseEntity.ok(CollectionModel.of(restaurantFavoriteService.findAllByUserId(loginId)
                .stream().map(dto -> EntityModel.of(dto)
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(dto.getId()).withRel("cancel-favorite"))
                        .add(restaurantLinker.findById(dto.getId()).withRel("restaurant-info"))
                ).collect(Collectors.toList()))
                .add(restaurantFavoriteLinker.findAllByUser().withSelfRel()));
    }

}
