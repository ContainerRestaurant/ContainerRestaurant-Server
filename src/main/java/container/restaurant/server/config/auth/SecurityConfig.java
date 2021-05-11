package container.restaurant.server.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final static String[] PERMITTED_ALL_PATH = {
            "/",
            "/docs/index.html",
            "/auth/list",
            "/api/user/nickname/**/exists",
            "/api/feed/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers(h -> h
                        .frameOptions().disable()
                )
                .authorizeRequests(a -> a
                        .antMatchers(PERMITTED_ALL_PATH).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(l -> l
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(o -> o
                        .userInfoEndpoint().userService(customOAuth2UserService)
                );
    }
}
