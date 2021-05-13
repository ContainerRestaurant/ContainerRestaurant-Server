package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseUserAndFeedControllerTest extends BaseUserControllerTest {

    @Autowired
    protected FeedRepository feedRepository;

    @Autowired
    protected RestaurantRepository restaurantRepository;

    protected Restaurant restaurant;
    protected Feed myFeed;
    protected Feed othersFeed;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .image_ID(1l)
                .build());
        myFeed = feedRepository.save(Feed.builder()
                .owner(myself)
                .restaurant(restaurant)
                .difficulty(4)
                .category(Category.JAPANESE)
                .welcome(true)
                .thumbnailUrl("https://my.thumbnail")
                .content("Feed Content")
                .build());
        othersFeed = feedRepository.save(Feed.builder()
                .owner(other)
                .restaurant(restaurant)
                .difficulty(3)
                .category(Category.KOREAN)
                .welcome(false)
                .thumbnailUrl("https://others.thumbnail")
                .build());
    }

    @Override
    @AfterEach
    public void afterEach() {
        feedRepository.delete(myFeed);
        feedRepository.delete(othersFeed);
        restaurantRepository.delete(restaurant);
        super.afterEach();
    }

}
