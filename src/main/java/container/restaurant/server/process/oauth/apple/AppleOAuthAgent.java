package container.restaurant.server.process.oauth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.exception.UnauthorizedException;
import container.restaurant.server.process.oauth.OAuthAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static container.restaurant.server.config.auth.user.CustomOAuth2User.REGISTRATION;
import static container.restaurant.server.domain.user.OAuth2Registration.APPLE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.of;

public class AppleOAuthAgent implements OAuthAgent {

    private final static String AUTH_KEY_URL = "https://appleid.apple.com/auth/keys";

    private final WebClient webClient;

    public AppleOAuthAgent() {
        this(WebClient.create(AUTH_KEY_URL));
    }

    public AppleOAuthAgent(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Optional<OAuthAttributes> getAuthAttrFrom(String accessToken) {
        Map<String, String> tokenHeader = getTokenHeader(accessToken);

        AppleAuthKeys authKeys = getPublicKeys();
        PublicKey publicKey = authKeys.publicKeyFrom(tokenHeader);

        Map<String, Object> attrs = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        attrs.put(REGISTRATION, APPLE);
        return of(OAuthAttributes.ofApple(attrs));
    }

    private Map<String, String> getTokenHeader(String accessToken) {
        String tokenHeaderRaw = accessToken.substring(0, accessToken.indexOf('.'));
        String tokenHeaderStr = new String(Base64.getUrlDecoder().decode(tokenHeaderRaw), UTF_8);
        try {
            //noinspection unchecked
            return new ObjectMapper().readValue(tokenHeaderStr, Map.class);
        } catch (JsonProcessingException e) {
            throw new UnauthorizedException("액세스 토큰 인증에 실패했습니다. (헤더 파싱 실패)", e);
        }
    }

    AppleAuthKeys getPublicKeys() {
        return webClient
                .get()
                .retrieve()
                .bodyToMono(AppleAuthKeys.class)
                .block();
    }

    @Override
    public CustomOAuth2User getAuthUserFrom(String accessToken) {
        return CustomOAuth2User.from(getAuthAttrFrom(accessToken)
                .orElseThrow(() -> new UnauthorizedException("액세스 토큰 인증에 실패했습니다."))
                .getAttributes());
    }
}
