package container.restaurant.server.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .headers(h -> h
                        .frameOptions().disable())
                .authorizeRequests(a -> a
                        .antMatchers(GET).permitAll()
                        .antMatchers(POST, "/api/user/login", "/api/user").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, OAuth2LoginAuthenticationFilter.class);
    }
}
