package container.restaurant.server.utils.jwt.jjwt;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.exception.UnauthorizedException;
import container.restaurant.server.utils.jwt.JwtLoginService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Key;

public class JjwtLoginService implements JwtLoginService {

    public final static Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String tokenize(OAuth2User user) {
        return Jwts.builder().setClaims(user.getAttributes()).signWith(KEY).compact();
    }

    @Override
    public CustomOAuth2User parse(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return CustomOAuth2User.from(claimsJws.getBody());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("만료된 토큰입니다.");
        } catch (JwtException e) {
            throw new UnauthorizedException("잘못된 토큰입니다.", e);
        }
    }

}
