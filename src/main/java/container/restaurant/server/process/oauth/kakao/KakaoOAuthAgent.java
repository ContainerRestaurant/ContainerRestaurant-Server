package container.restaurant.server.process.oauth.kakao;

import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.process.oauth.OAuthAgent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class KakaoOAuthAgent implements OAuthAgent {

    private final static String BASE_URL = "https://kapi.kakao.com/v2/user/me";

    private final WebClient webClient;

    public KakaoOAuthAgent() {
        this(WebClient.create(BASE_URL));
    }

    public KakaoOAuthAgent(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Optional<OAuthAttributes> getAuthAttrFrom(String accessToken) {
        return ofNullable(webClient
                    .get()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, String.join("", "Bearer ", accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block())
                .map(OAuthAttributes::ofKakao);
    }

    @Override
    public CustomOAuth2User getAuthUserFrom(String accessToken) {
        //noinspection unchecked
        return CustomOAuth2User.newUser(
                "kakao", "id",
                webClient
                        .get()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, String.join("", "Bearer ", accessToken))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block());
    }
}
