package container.restaurant.server.web.dto;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.banner.BannerInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Getter
public class IndexDto extends RepresentationModel<IndexDto> {

    private final Long loginId;
    private final Integer myContainer;
    private final Long totalContainer;
    private final Integer myLevel;
    private final String phrase;

    @Builder
    protected IndexDto(User user, Long totalContainer, String phrase) {
        this(
                from(user, User::getId, null),
                from(user, User::getFeedCount, 0),
                totalContainer,
                from(user, User::getLevel, 0),
                phrase);
    }

    private static <T> T from(User user, Function<User, T> function, T defaultValue) {
        return ofNullable(user)
                .map(function)
                .orElse(defaultValue);
    }
}
