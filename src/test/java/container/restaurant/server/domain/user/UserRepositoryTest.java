package container.restaurant.server.domain.user;

import container.restaurant.server.domain.feed.Container;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.menu.Menu;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.ArrayList;
import java.util.List;

import static container.restaurant.server.domain.feed.Category.KOREAN;
import static container.restaurant.server.domain.user.OAuth2Identifier.of;
import static container.restaurant.server.domain.user.OAuth2Registration.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(showSql = false)
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("AuthId 와 Provider 로 찾을 수 있다.")
    void testFindByEmail() {
        //given
        String email = "test@test";
        String authId = "authId";
        User newUser = userRepository.save(User.builder()
                .identifier(of(authId, KAKAO))
                .nickname("testNickname")
                .email(email)
                .build());

        //when
        User found = userRepository.findByIdentifier(newUser.getIdentifier())
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
                .identifier(of("authId", KAKAO))
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
                .identifier(of("authId", KAKAO))
                .email("test@test")
                .nickname(nickname)
                .build();
        assertThat(userRepository.existsUserByNickname(nickname)).isFalse();

        //when
        userRepository.save(user1);

        //then
        assertThat(userRepository.existsUserByNickname(nickname)).isTrue();
    }

    @Test
    @DisplayName("최근 x 개의 피드 작성자 조회 - 마지막 피드 기준 정렬")
    void 최근_x개의_피드_작성자_조회__마지막_피드_기준_정렬() {
        //given 2-1-2-3-1-3-1 유저 순으로 피드를 생성했을 때
        User user1 = em.persist(User.builder().identifier(of("TEST1", KAKAO)).build());
        User user2 = em.persist(User.builder().identifier(of("TEST2", KAKAO)).build());
        User user3 = em.persist(User.builder().identifier(of("TEST3", KAKAO)).build());
        Restaurant restaurant = em.persist(Restaurant.builder().lat(1f).lon(1f).addr("address").name("name").build());

        persistFeedWith(user2, restaurant);
        persistFeedWith(user1, restaurant);
        persistFeedWith(user2, restaurant);
        persistFeedWith(user3, restaurant);
        persistFeedWith(user1, restaurant);
        persistFeedWith(user3, restaurant);
        persistFeedWith(user1, restaurant);

        em.flush();em.clear();

        //when 최근 10 개의 피드 작성자 조회하면
        List<UserProfileDto> result = userRepository.findLatestUsers(PageRequest.of(0, 10));

        //then 가장 최신 피드 순인 1-3-2 순으로 반환됨 / 3 명의 유저가 있으므로 3의 크기를 갖음
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getId()).isEqualTo(user1.getId());
        assertThat(result.get(1).getId()).isEqualTo(user3.getId());
        assertThat(result.get(2).getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("최근 x 개의 피드 작성자 조회 - 피드 작성자만 조회")
    void 최근_x개의_피드_작성자_조회__피드_작성자만_조회() {
        //given 3 명의 유저가 주어졌을 때, 1, 2 번째 유저만 피드를 작성했으면
        User user1 = em.persist(User.builder().identifier(of("TEST1", KAKAO)).build());
        User user2 = em.persist(User.builder().identifier(of("TEST2", KAKAO)).build());
        User user3 = em.persist(User.builder().identifier(of("TEST3", KAKAO)).build());
        Restaurant restaurant = em.persist(Restaurant.builder().lat(1f).lon(1f).addr("address").name("name").build());

        persistFeedWith(user1, restaurant);
        persistFeedWith(user2, restaurant);

        em.flush();em.clear();

        //when 최근 10개의 피드 작성자를 조회하면
        List<UserProfileDto> result = userRepository.findLatestUsers(PageRequest.of(0, 10));

        //then 2 의 크기를 갖음 / 1, 2 번째 유저가 포함되고 3번째 유저는 포함되지 않음
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .anyMatch(dto -> dto.getId().equals(user1.getId()))
                .anyMatch(dto -> dto.getId().equals(user2.getId()))
                .allMatch(dto -> !dto.getId().equals(user3.getId()));
    }

    private void persistFeedWith(User user, Restaurant restaurant) {
        em.persist(Feed.builder().owner(user).restaurant(restaurant).category(KOREAN).difficulty(3)
                .menus(List.of(Container.of(Menu.mainOf(restaurant, "menu1"), "description1")))
                .build());
    }

}