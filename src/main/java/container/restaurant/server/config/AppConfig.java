package container.restaurant.server.config;

import container.restaurant.server.config.auth.JwtTokenFilter;
import container.restaurant.server.process.oauth.OAuthAgentFactory;
import container.restaurant.server.utils.jwt.JwtLoginService;
import container.restaurant.server.utils.jwt.jjwt.JjwtLoginService;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
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

    @Bean
    public HttpTraceRepository getHttpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    public JwtLoginService jwtParser() {
        return new JjwtLoginService();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(JwtLoginService jwtLoginService) {
        return new JwtTokenFilter(jwtLoginService);
    }
}
