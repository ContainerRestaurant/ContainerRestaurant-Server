package container.restaurant.server.domain.user;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.function.BiFunction;

import static container.restaurant.server.config.auth.user.CustomOAuth2User.*;

@RequiredArgsConstructor
public enum OAuth2Registration {

    TEST((ignore1, ignore2) ->  {
        LocalDateTime now = LocalDateTime.now();
        return Map.of(
                SUBJECT, "TEST",
                EXPIRATION_TIME, getLongTime(now.plusMonths(1)),
                ISSUED_AT, getLongTime(now),
                REGISTRATION, "TEST"
        );
    }),
    KAKAO((userNameAttributeName, attributes) -> {
        //noinspection unchecked
        Map<String, String> kakaoAccount = (Map<String, String>)
                attributes.getOrDefault("kakao_account", Map.of());

        LocalDateTime now = LocalDateTime.now();

        return Map.of(
                SUBJECT, attributes.get(userNameAttributeName),
                EXPIRATION_TIME, getLongTime(now.plusMonths(1)),
                ISSUED_AT, getLongTime(now),
                REGISTRATION, "KAKAO",
                EMAIL, kakaoAccount.getOrDefault("email", "")
        );
    }),
    APPLE((String userNameAttributeName, Map<String, Object> attributes) -> {
        LocalDateTime now = LocalDateTime.now();

        return Map.of(
                SUBJECT, attributes.get("sub"),
                EXPIRATION_TIME, getLongTime(now.plusMonths(1)),
                ISSUED_AT, getLongTime(now),
                REGISTRATION, "APPLE",
                EMAIL, attributes.getOrDefault("email", ""));
    }),
    GOOGLE((String userNameAttributeName, Map<String, Object> attributes) -> {
        throw new UnsupportedOperationException("아직 지원하지 않는 로그인 제공자입니다.(GOOGLE)");});

    private final BiFunction<String, Map<String, Object>, Map<String, Object>> extractionFunction;

    public Map<String, Object> extractAuthInfo(String userNameAttributeName, Map<String, Object> attributes) {
        return extractionFunction.apply(userNameAttributeName, attributes);
    }

    private static Long getLongTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
