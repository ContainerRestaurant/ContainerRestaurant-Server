package container.restaurant.server.web.dto.restaurant;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class RestaurantNearInfoDto extends RepresentationModel<RestaurantNearInfoDto> {

    private final Long id;
    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final String image_path;

    public static RestaurantNearInfoDto from(Restaurant restaurant, Image image) {
        return new RestaurantNearInfoDto(restaurant, image);
    }

    protected RestaurantNearInfoDto(Restaurant restaurant, Image image) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.image_path = image.getUrl();
    }

}
