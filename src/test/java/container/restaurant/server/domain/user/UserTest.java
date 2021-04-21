package container.restaurant.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UserTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        String email = "test@test.com";
        String nickname = "nick";
        String profile = "profilePath";
        String greeting = "Hello! Container!";

        //when
        User user = User.builder()
                .email(email)
                .profile(profile)
                .build();

        //then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEmpty();
        assertThat(user.getProfile()).isEqualTo(profile);
        assertThat(user.getGreeting()).isNull();
        assertThat(user.getLevel()).isEqualTo(1);
        assertThat(user.getExperience()).isEqualTo(0);
        assertThat(user.getFeedCount()).isEqualTo(0);
        assertThat(user.getBanned()).isFalse();
    }

}