package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "TB_MENU")
public class Menu extends BaseEntity {

    @NotNull
    @ManyToOne
    private Restaurant restaurant;

    @NotNull
    @OneToOne
    private Container container;

    public static Menu of(Restaurant restaurant, Container container) {
        return new Menu(restaurant, container);
    }
}
