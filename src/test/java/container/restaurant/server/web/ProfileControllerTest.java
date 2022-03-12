package container.restaurant.server.web;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ProfileControllerTest {

    @ParameterizedTest
    @MethodSource
    void profileTest(String expectedProfile, List<String> profiles) {
        //given
        MockEnvironment env = new MockEnvironment();
        profiles.forEach(env::addActiveProfile);

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    static Stream<Arguments> profileTest() {
        return Stream.of(
                arguments("real", List.of("real", "oauth", "deploy-datasource")),
                arguments("oauth", List.of("oauth", "real-db")),
                arguments("default", List.of())
        );
    }
}