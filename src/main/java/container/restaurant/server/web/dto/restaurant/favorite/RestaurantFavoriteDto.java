package container.restaurant.server.web.dto.restaurant.favorite;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavorite;
import container.restaurant.server.web.dto.restaurant.RestaurantNearInfoDto;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.format.DateTimeFormatter;

@Getter
public class RestaurantFavoriteDto extends RepresentationModel<RestaurantFavoriteDto> {

    private final Long id;
    private final String createDate;
    private final RestaurantNearInfoDto restaurant;

    public static RestaurantFavoriteDto from(RestaurantFavorite restaurantFavorite, Image image) {
        return new RestaurantFavoriteDto(restaurantFavorite, image);
    }

    protected RestaurantFavoriteDto(RestaurantFavorite restaurantFavorite, Image image) {
        this.id = restaurantFavorite.getId();
        this.createDate = restaurantFavorite.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.restaurant = RestaurantNearInfoDto.from(restaurantFavorite.getRestaurant(), image);
    }

}
