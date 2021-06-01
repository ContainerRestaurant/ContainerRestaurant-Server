package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.feed.container.ContainerRepository;
import container.restaurant.server.domain.feed.container.ContainerService;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.restaurant.menu.Menu;
import container.restaurant.server.domain.restaurant.menu.MenuRepository;
import container.restaurant.server.domain.user.level.UserLevelFeedCountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseUserAndFeedControllerTest extends BaseUserControllerTest {

    @Autowired
    protected FeedRepository feedRepository;

    @Autowired
    protected RestaurantRepository restaurantRepository;

    @Autowired
    protected ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ContainerService containerService;

    @Autowired
    protected UserLevelFeedCountRepository userLevelFeedCountRepository;

    protected Restaurant restaurant;
    protected Feed myFeed;
    protected Feed othersFeed;
    protected Image image;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();

        image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        restaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .image_ID(image.getId())
                .build());
        myFeed = feedRepository.save(Feed.builder()
                .owner(myself)
                .restaurant(restaurant)
                .difficulty(4)
                .category(Category.JAPANESE)
                .welcome(true)
                .thumbnail(image)
                .content("Feed Content")
                .build());
        othersFeed = feedRepository.save(Feed.builder()
                .owner(other)
                .restaurant(restaurant)
                .difficulty(3)
                .category(Category.KOREAN)
                .welcome(false)
                .thumbnail(image)
                .build());
        myFeed.setContainerList(containerService.save(List.of(
                Container.of(myFeed, Menu.mainOf(restaurant, "나의 메인 메뉴1"), "나의 메인 용기1"),
                Container.of(myFeed, Menu.mainOf(restaurant, "나의 메인 메뉴2"), "나의 메인 용기2"),
                Container.of(myFeed, Menu.subOf(restaurant, "나의 반찬 메뉴1"), "나의 반찬 용기1"),
                Container.of(myFeed, Menu.subOf(restaurant, "나의 반찬 메뉴2"), "나의 반찬 용기2")
        )));
        othersFeed.setContainerList(containerService.save(List.of(
                Container.of(othersFeed, Menu.mainOf(restaurant, "남의 메인 메뉴1"), "남의 메인 용기1"),
                Container.of(othersFeed, Menu.mainOf(restaurant, "남의 메인 메뉴2"), "남의 메인 용기2"),
                Container.of(othersFeed, Menu.subOf(restaurant, "남의 반찬 메뉴1"), "남의 반찬 용기1"),
                Container.of(othersFeed, Menu.subOf(restaurant, "남의 반찬 메뉴2"), "남의 반찬 용기2")
        )));
    }

    @Override
    @AfterEach
    public void afterEach() {
        containerRepository.deleteAll();
        menuRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        imageRepository.deleteAll();
        userLevelFeedCountRepository.deleteAll();
        super.afterEach();
    }

}
