package container.restaurant.server.config;

import container.restaurant.server.process.oauth.OAuthAgentFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@Configuration
public class AppConfig {

    @Bean
    public OAuthAgentFactory getOAuthAgentFactory() {
        return OAuthAgentFactory.createDefaultFactory();
    }

}
