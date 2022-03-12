package container.restaurant.server.config;

import container.restaurant.server.web.linker.FeedLikeLinker;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import container.restaurant.server.web.linker.ScrapFeedLinker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkerConfig {

    @Bean
    public ScrapFeedLinker userScrapLinker() {
        return new ScrapFeedLinker();
    }

    @Bean
    public FeedLikeLinker feedLikeLinker() {
        return new FeedLikeLinker();
    }

    @Bean
    public RestaurantFavoriteLinker restaurantFavoriteLinker() {
        return new RestaurantFavoriteLinker();
    }

}
