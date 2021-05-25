package container.restaurant.server.domain.feed.container;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.menu.Menu;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "TB_CONTAINER")
public class Container extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;

    private String description;

    public static Container of(Feed feed, Menu menu, String description) {
        return new Container(feed, menu, description);
    }

    public Container setMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

}
