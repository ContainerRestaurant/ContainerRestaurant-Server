package container.restaurant.server.config;

import container.restaurant.server.config.auth.LoginUserArgumentResolver;
import container.restaurant.server.domain.feed.CategoryArgumentResolver;
import container.restaurant.server.web.util.CustomSortArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;
    private final CategoryArgumentResolver categoryArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
        resolvers.add(categoryArgumentResolver);
        resolvers.add(new PageableHandlerMethodArgumentResolver(new CustomSortArgumentResolver()));
    }

}
