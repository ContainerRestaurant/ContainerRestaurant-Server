package container.restaurant.server.domain.restaurant.favorite;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "tb_favorite_restaurant")
public class RestaurantFavorite extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private Restaurant restaurant;

    public static RestaurantFavorite of(User user, Restaurant restaurant) {
        return new RestaurantFavorite(user, restaurant);
    }
}
