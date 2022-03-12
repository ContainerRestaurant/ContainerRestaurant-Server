package container.restaurant.server.domain.user;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.web.dto.feed.LevelUpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static container.restaurant.server.domain.user.ContainerLevel.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class UserTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        String authId = "testId";
        OAuth2Registration provider = OAuth2Registration.KAKAO;
        String email = "test@test.com";
        String nickname = "testNickname";
        Image profile = new Image("profilePath");
        PushToken pushToken = new PushToken("testToken");

        //when
        User user = User.builder()
                .identifier(OAuth2Identifier.of(authId, provider))
                .email(email)
                .nickname(nickname)
                .profile(profile)
                .pushToken(pushToken)
                .build();

        //then
        assertThat(user.getIdentifier()).isEqualTo(OAuth2Identifier.of(authId, provider));
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getProfile().getUrl()).isEqualTo(profile.getUrl());
        assertThat(user.getLevelTitle()).isEqualTo(LEVEL_1.getTitle());
        assertThat(user.getLevelFeedCount()).isEqualTo(0);
        assertThat(user.getFeedCount()).isEqualTo(0);
        assertThat(user.getBanned()).isFalse();
        assertThat(user.getPushToken()).isEqualTo(pushToken);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("레벨링 테스트")
    void testLeveling(int before, int count, boolean up, ContainerLevel expectedLevel) throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = new User();
        Field f = User.class.getDeclaredField("levelFeedCount");
        f.setAccessible(true);
        f.set(user, before);

        //when
        if (up) user.levelFeedUp(count);
        else user.levelFeedDown(count);

        //then
        assertThat(user.getContainerLevel()).isEqualTo(expectedLevel);
    }

    static Stream<Arguments> testLeveling() {
        return Stream.of(
                arguments(3, 3, false, LEVEL_1),
                arguments(3, 7, true, LEVEL_4),
                arguments(25, 6, false, LEVEL_4),
                arguments(0, 5, true, LEVEL_3)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("레벨 업 테스트")
    void testLevelUp(int count, ContainerLevel expectedLevel) throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = new User();
        Field f = User.class.getDeclaredField("levelFeedCount");
        f.setAccessible(true);
        f.set(user, 0);

        //when
        LevelUpDto result = user.levelFeedUp(count);

        //then
        if (expectedLevel == null)
            assertThat(result).isNull();
        else {
            assertThat(result.getTo()).isEqualTo(expectedLevel.getTitle());
            assertThat(result.getLevelFeedCount()).isEqualTo(expectedLevel.getNeedCount());
        }
    }

    static Stream<Arguments> testLevelUp() {
        return Stream.of(
                arguments(5, LEVEL_3),
                arguments(10, LEVEL_4),
                arguments(11, null),
                arguments(12, null)
        );
    }

}