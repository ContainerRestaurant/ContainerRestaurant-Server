package container.restaurant.server.web.dto.restaurant.favorite;

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

    public static RestaurantFavoriteDto from(RestaurantFavorite restaurantFavorite) {
        return new RestaurantFavoriteDto(restaurantFavorite);
    }

    protected RestaurantFavoriteDto(RestaurantFavorite restaurantFavorite) {
        this.id = restaurantFavorite.getId();
        this.createDate = restaurantFavorite.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.restaurant = RestaurantNearInfoDto.from(restaurantFavorite.getRestaurant(), true);
        // 즐겨찾기한 정보를 불러오는 것이기에 무조건 isFavorite 은 true
    }

}
