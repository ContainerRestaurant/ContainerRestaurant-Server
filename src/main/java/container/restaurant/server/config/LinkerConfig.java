package container.restaurant.server.config;

import container.restaurant.server.web.linker.FeedLikeLinker;
import container.restaurant.server.web.linker.UserScrapLinker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkerConfig {

    @Bean
    public UserScrapLinker userScrapLinker() {
        return new UserScrapLinker();
    }

    @Bean
    public FeedLikeLinker feedLikeLinker() {
        return new FeedLikeLinker();
    }

}
