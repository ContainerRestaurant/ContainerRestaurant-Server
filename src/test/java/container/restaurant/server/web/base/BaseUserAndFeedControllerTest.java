package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.locationtech.jts.geom.Point;
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
                .build());
        myFeed = feedRepository.save(Feed.builder()
                .owner(myself)
                .restaurant(restaurant)
                .difficulty(4)
                .welcome(true)
                .build());
        othersFeed = feedRepository.save(Feed.builder()
                .owner(other)
                .restaurant(restaurant)
                .difficulty(3)
                .welcome(false)
                .build());
    }

    @Override
    @AfterEach
    public void afterEach() {
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        super.afterEach();
    }

}
