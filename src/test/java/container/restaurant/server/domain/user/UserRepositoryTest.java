package container.restaurant.server.domain.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("AuthId 와 Provider 로 찾을 수 있다.")
    void testFindByEmail() {
        //given
        String email = "test@test";
        String authId = "authId";
        AuthProvider provider = AuthProvider.KAKAO;
        User newUser = userRepository.save(User.builder()
                .authId(authId)
                .authProvider(provider)
                .nickname("testNickname")
                .email(email)
                .build());

        //when
        User found = userRepository.findByAuthProviderAndAuthId(provider, authId)
                .orElse(null);

        //then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(newUser.getId());
        assertThat(found.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("닉네임 null 테스트")
    void testNickNameNullable() {
        //given
        final User user1 = User.builder()
                .email("test1@test")
                .build();

        //expect
        assertThatThrownBy(() -> userRepository.save(user1));
    }

    @Test
    @DisplayName("닉네임 중복 불가 테스트")
    void testDuplicatedNickname() {
        //given
        User user1 = User.builder()
                .authId("authId")
                .authProvider(AuthProvider.KAKAO)
                .email("test1@test")
                .build();
        final User user2 = User.builder()
                .email("test2@test")
                .build();
        String nickname = "testNick";
        user1.setNickname(nickname);
        user2.setNickname(nickname);

        //when
        user1 = userRepository.save(user1);

        //then
        assertThat(user1.getNickname()).isEqualTo(nickname);
        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("닉네인 중복 확인 테스트")
    void testExistsNickname() {
        //given
        String nickname = "추가된닉네임";
        User user1 = User.builder()
                .authId("authId")
                .authProvider(AuthProvider.KAKAO)
                .email("test@test")
                .nickname(nickname)
                .build();
        assertThat(userRepository.existsUserByNickname(nickname)).isFalse();

        //when
        userRepository.save(user1);

        //then
        assertThat(userRepository.existsUserByNickname(nickname)).isTrue();
    }

}