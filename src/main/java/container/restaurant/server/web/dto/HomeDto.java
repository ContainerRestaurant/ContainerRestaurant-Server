package container.restaurant.server.web.dto;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.statistics.UserProfileDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final String myProfile;
    private final String phrase;

    private final List<String> latestWriterProfile = new ArrayList<>();
    private final List<HomeBannerDto> banners = new ArrayList<>();

    @Builder
    protected HomeDto(User user, Long totalContainer, String phrase, Collection<UserProfileDto> latestWriters, List<HomeBannerDto> banners) {
        this(
                from(user, User::getId, null),
                from(user, User::getFeedCount, 0),
                totalContainer,
                from(user, User::getLevelTitle, LEVEL_1.getTitle()),
                from(user, user1 -> user1.getProfile().getUrl(), null),
                phrase);

        latestWriters.stream().limit(3).forEach(u ->
                this.latestWriterProfile.add(u.getProfile()));

        this.banners.addAll(banners);
    }

    private static <T> T from(User user, Function<User, T> function, T defaultValue) {
        return ofNullable(user)
                .map(function)
                .orElse(defaultValue);
    }
}
