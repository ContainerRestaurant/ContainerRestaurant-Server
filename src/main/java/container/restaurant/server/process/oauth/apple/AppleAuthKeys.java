package container.restaurant.server.process.oauth.apple;

import container.restaurant.server.exception.UnauthorizedException;
import lombok.Getter;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Getter
public class AppleAuthKeys {

    private List<Map<String, String>> keys;

    public PublicKey publicKeyFrom(Map<String, String> tokenHeader) {
        Map<String, String> key = getKey(tokenHeader.get("kid"), tokenHeader.get("alg"));
        return generatePublicKey(key);
    }

    private Map<String, String> getKey(String kid, String alg) {
        return keys.stream()
                .filter(it -> kid.equals(it.get("kid")) && alg.equals(it.get("alg")))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("액세스 토큰 인증에 실패했습니다. (kid, alg 불일치)"));
    }

    private PublicKey generatePublicKey(Map<String, String> key) {
        BigInteger n = keySpec(key.get("n"));
        BigInteger e = keySpec(key.get("e"));

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(key.get("kty"));
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new UnauthorizedException("공개키 생성에 실패했습니다.", ex);
        }
    }

    private BigInteger keySpec(String key) {
        return new BigInteger(1, Base64.getUrlDecoder().decode(key));
    }

}
