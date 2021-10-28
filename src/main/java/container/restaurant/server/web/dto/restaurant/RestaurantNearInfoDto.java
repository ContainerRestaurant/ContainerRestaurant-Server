package container.restaurant.server.web.dto.restaurant;

import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.utils.ImageUtils;
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
    private final Integer feedCount;
    private final Float difficultyAvg;
    private final Boolean isContainerFriendly;
    private final Boolean isFavorite;

    public static RestaurantNearInfoDto from(Restaurant restaurant, Boolean isFavorite) {
        return new RestaurantNearInfoDto(restaurant, isFavorite);
    }

    protected RestaurantNearInfoDto(Restaurant restaurant, Boolean isFavorite) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.feedCount = restaurant.getFeedCount();
        this.difficultyAvg = restaurant.getDifficultyAvg();
        this.image_path = ImageUtils.getUrlFromImage(restaurant.getThumbnail());
        this.isContainerFriendly = restaurant.isContainerFriendly();
        this.isFavorite = isFavorite;

    }

}
