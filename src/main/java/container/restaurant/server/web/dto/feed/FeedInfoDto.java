package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class FeedInfoDto {

    @NotNull
    private final Restaurant restaurant;

    @NotNull
    private final Category category;

    private final List<FeedMenuDto> mainMenu;
    private final List<FeedMenuDto> subMenu;

    @NotNull
    private final Integer difficulty;

    private final Boolean welcome;
    private final Long thumbnailImageId;
    private final String content;

    public Feed toFeedWith(User owner, Restaurant restaurant) {
        return Feed.builder()
                .owner(owner)
                .restaurant(restaurant)
                .category(category)
                .thumbnailImageId(thumbnailImageId)
                .content(content)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();
    }

    public List<Container> toContainerListWith(Feed feed, Restaurant restaurant) {
        List<Container> list = new ArrayList<>(mainMenu.size() + subMenu.size());

        mainMenu.stream()
                .map(feedMenuDto -> feedMenuDto.toEntity(feed, restaurant, true))
                .forEachOrdered(list::add);

        subMenu.stream()
                .map(feedMenuDto -> feedMenuDto.toEntity(feed, restaurant, false))
                .forEachOrdered(list::add);

        return list;
    }

    public Feed update(Feed feed) {
        if (!category.equals(feed.getCategory()))
            feed.setCategory(category);
        if (!difficulty.equals(feed.getDifficulty()))
            feed.setDifficulty(difficulty);
        if (!welcome.equals(feed.getWelcome()))
            feed.setWelcome(welcome);
        if (!thumbnailImageId.equals(feed.getThumbnailImageId()))
            feed.setThumbnailImageId(thumbnailImageId);
        if (!content.equals(feed.getContent()))
            feed.setContent(content);
        return feed;
    }

}
