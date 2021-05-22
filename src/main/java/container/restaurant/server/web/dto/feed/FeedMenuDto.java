package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FeedMenuDto {

    private final String menuName;
    private final String container;

    public static FeedMenuDto from(Container container) {
        return new FeedMenuDto(
                container.getMenu().getName(),
                container.getDescription());
    }

    public Container toEntity(Feed feed, Restaurant restaurant, Boolean isMain) {
        return Container.of(feed, Menu.of(restaurant, menuName, isMain), container);
    }
}
