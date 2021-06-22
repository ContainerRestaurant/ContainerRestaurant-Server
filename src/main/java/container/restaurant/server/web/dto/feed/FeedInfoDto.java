package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@AllArgsConstructor
public class FeedInfoDto {

    @NotNull
    private final RestaurantInfoDto restaurantCreateDto;

    @NotNull
    private final Category category;

    private final List<FeedMenuDto> mainMenu;
    private final List<FeedMenuDto> subMenu;

    @NotNull
    private final Integer difficulty;

    private final Boolean welcome;
    private final Long thumbnailImageId;
    private final String content;

    public Feed toFeedWith(User owner, Restaurant restaurant, Image thumbnail) {
        Feed newFeed = Feed.builder()
                .owner(owner)
                .restaurant(restaurant)
                .category(category)
                .thumbnail(thumbnail)
                .content(content)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();
        newFeed.updateContainers(toContainerListWith(newFeed, restaurant));

        return newFeed;
    }

    public List<Container> toContainerListWith(Feed feed, Restaurant restaurant) {
        int initialCapacity = mainMenu.size() + (subMenu != null ? subMenu.size() : 0);
        List<Container> list = new ArrayList<>(initialCapacity);

        mainMenu.forEach(feedMenuDto -> list.add(feedMenuDto.toEntity(feed, restaurant, true)));

        Optional.ofNullable(subMenu).ifPresent(l ->
                l.forEach(feedMenuDto -> list.add(feedMenuDto.toEntity(feed, restaurant, false))));

        return list;
    }

    public void updateSimpleAttrs(Feed feed) {
        if (!category.equals(feed.getCategory()))
            feed.setCategory(category);
        if (!difficulty.equals(feed.getDifficulty()))
            feed.setDifficulty(difficulty);
        if (!welcome.equals(feed.getWelcome()))
            feed.setWelcome(welcome);
        if (!content.equals(feed.getContent()))
            feed.setContent(content);
    }

}
