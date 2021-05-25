package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@AllArgsConstructor
public class FeedInfoDto {

    @NotNull
    private final Long restaurantId;

    @NotNull
    private final Category category;

    // TODO Menu

    @NotNull
    private final Integer difficulty;

    private final Boolean welcome;
    private final String thumbnailUrl;
    private final String content;

    // TODO Menu
    public Feed toEntityWith(User owner, Restaurant restaurant) {
        return Feed.builder()
                .owner(owner)
                .restaurant(restaurant)
                .category(category)
                .thumbnailUrl(thumbnailUrl)
                .content(content)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();
    }

    // TODO Menu
    public Feed update(Feed feed) {
        if (!category.equals(feed.getCategory()))
            feed.setCategory(category);
        if (!difficulty.equals(feed.getDifficulty()))
            feed.setDifficulty(difficulty);
        if (!welcome.equals(feed.getWelcome()))
            feed.setWelcome(welcome);
        if (!thumbnailUrl.equals(feed.getThumbnailUrl()))
            feed.setThumbnailUrl(thumbnailUrl);
        if (!content.equals(feed.getContent()))
            feed.setContent(content);
        return feed;
    }

}
