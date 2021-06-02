package container.restaurant.server.web.dto.restaurant;

import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantInfoDto {

    private final Long id;
    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;

    public static RestaurantInfoDto from(Restaurant restaurant) {
        return new RestaurantInfoDto(restaurant);
    }

    protected RestaurantInfoDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
    }

    public Restaurant toEntity() {
        return Restaurant.builder()
                .name(name)
                .addr(address)
                .lat(latitude)
                .lon(longitude)
                .build();
    }

}
