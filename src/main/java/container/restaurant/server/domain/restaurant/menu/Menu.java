package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity(name = "TB_MENU")
public class Menu extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @NotNull
    private String name;

    @NotNull
    private Boolean isMain;

    private Integer count;

    public static Menu mainOf(Restaurant restaurant, String menuName) {
        return of(restaurant, menuName, true);
    }

    public static Menu subOf(Restaurant restaurant, String menuName) {
        return of(restaurant, menuName, false);
    }

    public static Menu of(Restaurant restaurant, String menuName, Boolean isMain) {
        return new Menu(restaurant, menuName, isMain);
    }

    protected Menu(Restaurant restaurant, String menuName, Boolean isMain) {
        this.restaurant = restaurant;
        this.name = menuName;
        this.count = 0;
        this.isMain = isMain;
    }

    public int countUp() {
        return ++this.count;
    }

    public int countDown() {
        return --this.count;
    }

}
