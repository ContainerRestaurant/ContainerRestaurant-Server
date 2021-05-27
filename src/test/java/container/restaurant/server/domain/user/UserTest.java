package container.restaurant.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class UserTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        String email = "test@test.com";
        String profile = "profilePath";

        //when
        User user = User.builder()
                .email(email)
                .profile(profile)
                .build();

        //then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isNull();
        assertThat(user.getProfile()).isEqualTo(profile);
        assertThat(user.getGreeting()).isNull();
        assertThat(user.getLevel()).isEqualTo(1);
        assertThat(user.getLevelFeedCount()).isEqualTo(0);
        assertThat(user.getFeedCount()).isEqualTo(0);
        assertThat(user.getBanned()).isFalse();
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("레벨링 테스트")
    void testLeveling(int before, int count, boolean up, int expectedLevel) throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = new User();
        Field f = User.class.getDeclaredField("levelFeedCount");
        f.setAccessible(true);
        f.set(user, before);

        //when
        if (up) user.levelFeedUp(count);
        else user.levelFeedDown(count);

        //then
        assertThat(user.getLevel()).isEqualTo(expectedLevel);
    }

    static Stream<Arguments> testLeveling() {
        return Stream.of(
                arguments(3, 3, false, 0),
                arguments(3, 7, true, 3),
                arguments(25, 6, false, 3),
                arguments(0, 5, true, 2)
        );
    }

}