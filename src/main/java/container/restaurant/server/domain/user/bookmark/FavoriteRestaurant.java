package container.restaurant.server.domain.user.bookmark;

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
@Entity(name = "TB_FAVORITE_RESTAURANT")
public class FavoriteRestaurant extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private Restaurant restaurant;

    public static FavoriteRestaurant of(User user, Restaurant restaurant) {
        return new FavoriteRestaurant(user, restaurant);
    }

}
