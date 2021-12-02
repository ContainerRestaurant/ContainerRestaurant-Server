package container.restaurant.server.domain.restaurant.dto;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;

public interface RestaurantThumbnailDto {

    Restaurant getRestaurant();
    Image getFeedThumbnail();
    Integer getLikeCount();

}
