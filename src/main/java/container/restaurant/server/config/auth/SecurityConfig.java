package container.restaurant.server.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final static String[] PERMITTED_ALL_PATH = {
            "/",
            "/banners",
            "/docs/index.html",
            "/api/contract",
            "/auth/list",
            "/api/user/nickname/**/exists",
            "/api/feed/**",
            "/api/comment/**",
            "/api/image/**",
            "/api/restaurant/**",
            "/api/statistics/**",
            "/api/push/**",
            "/profile"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers(h -> h
                        .frameOptions().disable()
                )
                // FIXME 임시 로그인 방편
                .authorizeRequests(a -> a
                        .anyRequest().permitAll()
//                        .antMatchers(HttpMethod.GET, PERMITTED_ALL_PATH).permitAll()
//                        .anyRequest().authenticated()
                )
                .logout(l -> l
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(o -> o
                        .userInfoEndpoint().userService(customOAuth2UserService)
                );
    }
}
