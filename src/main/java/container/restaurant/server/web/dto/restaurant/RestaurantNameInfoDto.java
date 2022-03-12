package container.restaurant.server.web.dto.restaurant;

import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class RestaurantNameInfoDto extends RepresentationModel<RestaurantNameInfoDto> {

    private final String name;
    private final Long id;

    public static RestaurantNameInfoDto from(Restaurant restaurant) {
        return new RestaurantNameInfoDto(restaurant);
    }

    protected RestaurantNameInfoDto(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.id = restaurant.getId();
    }

}
