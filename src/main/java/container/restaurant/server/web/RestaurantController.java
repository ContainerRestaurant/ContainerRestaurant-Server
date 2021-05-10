package container.restaurant.server.web;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.service.RestaurantService;
import container.restaurant.server.web.dto.user.NicknameExistsDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurant/")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public List<ResponseEntity<?>> findAll() {
        return restaurantService.findAll().stream().map(restaurant ->
                ResponseEntity.ok().body(
                        EntityModel.of(restaurant)
                                .add(linkTo(getController().findById(restaurant.getId())).withSelfRel()))
        ).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        try {
            Restaurant restaurant = restaurantService.findById(id);
            return ResponseEntity.ok(EntityModel.of(restaurant)
                    .add(linkTo(getController().findById(restaurant.getId())).withSelfRel())
            );
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{lat}/{lon}/{radius}")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    public List<ResponseEntity<?>> findNearByRestaurants(
            @PathVariable("lat") double lat,
            @PathVariable("lon") double lon,
            @PathVariable("radius") long radius) {
        /*
            사용자 정보를 PathVariable 형태로 받는 형태로 구현 추후 입력방식 변경 가능
         */
        return
                restaurantService.findNearByRestaurants(lat, lon, radius).stream().map(restaurant ->
                        ResponseEntity.ok(EntityModel.of(restaurant)
                                .add(linkTo(getController().findById(restaurant.getId())).withRel("restaurant-info"))
                                .add(linkTo(getController().findNearByRestaurants(lat, lon, radius)).withSelfRel()))
                ).collect(Collectors.toList());
    }

    private RestaurantController getController() {
        return methodOn(RestaurantController.class);
    }

}
