package container.restaurant.server.web.dto;

import container.restaurant.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.function.Function;

import static container.restaurant.server.domain.user.ContainerLevel.LEVEL_1;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Getter
public class HomeDto extends RepresentationModel<HomeDto> {

    private final Long loginId;
    private final Integer myContainer;
    private final Long totalContainer;
    private final String myLevelTitle;
    private final String phrase;

    @Builder
    protected HomeDto(User user, Long totalContainer, String phrase) {
        this(
                from(user, User::getId, null),
                from(user, User::getFeedCount, 0),
                totalContainer,
                from(user, User::getLevelTitle, LEVEL_1.getTitle()),
                phrase);
    }

    private static <T> T from(User user, Function<User, T> function, T defaultValue) {
        return ofNullable(user)
                .map(function)
                .orElse(defaultValue);
    }
}
