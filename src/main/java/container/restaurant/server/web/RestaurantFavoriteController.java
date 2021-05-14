package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteService;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/favorite/restaurant/")
public class RestaurantFavoriteController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    private final RestaurantFavoriteLinker restaurantFavoriteLinker;

    @PostMapping("{restaurantId}")
    public ResponseEntity<?> userFavoriteRestaurant(
            @LoginUser SessionUser sessionUser, @PathVariable Long restaurantId
    ) {
        restaurantFavoriteService.userFavoriteRestaurant(sessionUser.getId(), restaurantId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(restaurantFavoriteLinker.userFavoriteRestaurant(restaurantId).withSelfRel())
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(restaurantId).withRel("cancel-favorite"))
        );
    }

    @DeleteMapping("{restaurantId}")
    public ResponseEntity<?> userCancelFavoriteRestaurant(
            @LoginUser SessionUser sessionUser, @PathVariable Long restaurantId
    ) {
        restaurantFavoriteService.userCancelFavoriteRestaurant(sessionUser.getId(), restaurantId);
        return ResponseEntity.ok(
                HalModelBuilder.emptyHalModel().build()
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(restaurantId).withSelfRel())
                        .add(restaurantFavoriteLinker.userFavoriteRestaurant(restaurantId).withRel("favorite"))
        );
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<?>> userFindAllFavoriteRestaurant(@LoginUser SessionUser sessionUser) {
        restaurantFavoriteService.userFindAllFavoriteRestaurant(sessionUser.getId());

        return ResponseEntity.ok(new CollectionModel<>(restaurantFavoriteService.userFindAllFavoriteRestaurant(sessionUser.getId())
                .stream().map(favoriteRestaurantDto -> EntityModel.of(favoriteRestaurantDto)
                        .add(restaurantFavoriteLinker.userCancelFavoriteRestaurant(favoriteRestaurantDto.getId()).withRel("cancel-favorite"))
                        .add(restaurantFavoriteLinker.findRestaurantById(favoriteRestaurantDto.getRestaurant()).withRel("restaurant-info"))
                        .add(restaurantFavoriteLinker.restaurantImagePath(favoriteRestaurantDto.getRestaurant().getImage_path()).withRel("image-url"))
                ).collect(Collectors.toList()))
                .add(restaurantFavoriteLinker.userFindAllFavoriteRestaurant().withSelfRel()));
    }

}
