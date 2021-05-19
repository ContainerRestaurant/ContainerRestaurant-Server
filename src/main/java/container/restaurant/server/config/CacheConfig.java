package container.restaurant.server.config;

import container.restaurant.server.web.linker.FeedLikeLinker;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import container.restaurant.server.web.linker.ScrapFeedLinker;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

}
