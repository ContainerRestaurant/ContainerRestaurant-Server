package container.restaurant.server.domain.user;

import container.restaurant.server.config.auth.user.CustomOAuth2User;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteRepository;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.level.UserLevelFeedCountRepository;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.process.oauth.OAuthAgentService;
import container.restaurant.server.utils.jwt.JwtLoginService;
import container.restaurant.server.web.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FeedHitRepository feedHitRepository;
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final UserLevelFeedCountRepository userLevelFeedCountRepository;

    private final ImageService imageService;
    private final JwtLoginService jwtLoginService;
    private final OAuthAgentService oAuthAgentService;
    private final StatisticsService statisticsService;

    @Transactional
    public User createOrUpdate(
            OAuth2Identifier identifier, Supplier<@Valid ? extends User> supplier
    ) {
        User user = userRepository.findByIdentifier(identifier)
                .orElseGet(supplier);

        return userRepository.save(user);
    }

    @Transactional
    public UserDto.Token newToken(UserDto.ToRequestToken dto) {
        CustomOAuth2User authUser = oAuthAgentService.getAuthUser(dto);
        final User user = userRepository.findByIdentifier(authUser.getIdentifier())
                .orElseGet(() -> userRepository.save(
                        User.builder().identifier(authUser.getIdentifier()).build()));
        String newToken = jwtLoginService.tokenize(authUser);
        return new UserDto.Token(user.getId(), newToken, user.getNickname() == null);
    }

    @Transactional
    public Long getUserIdFromIdentifier(OAuth2Identifier identifier) {
        return userRepository.findByIdentifier(identifier)
                .map(User::getId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public UserDto.Info getUserInfoById(Long id) throws ResourceNotFoundException {

        return UserDto.Info.from(findById(id));
    }

    @Transactional
    public UserDto.Info update(Long id, UserDto.Update dto) {
        User user = findById(id);
        ofNullable(dto.getNickname()).ifPresent(user::setNickname);
        ofNullable(dto.getProfileId())
                .map(imageService::findById)
                .ifPresent(user::setProfile);
        ofNullable(dto.getPushToken())
                .ifPresent(user::setPushToken);

        statisticsService.afterUserUpdate(user);
        return UserDto.Info.from(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 사용자입니다.(id:" + id + ")"));
    }

    @Transactional
    public void deleteById(Long id) {
        restaurantFavoriteRepository.deleteAllByUserId(id);
        userLevelFeedCountRepository.deleteAllByUserId(id);
        feedHitRepository.deleteAllByUserId(id);

        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Boolean existsUserByNickname(String nickname) {
        return userRepository.existsUserByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public List<User> findByFeedCountTopUsers(LocalDateTime to, LocalDateTime from) {
        return userRepository.findByFeedCountTopUsers(to, from);
    }

    @Transactional(readOnly = true)
    public User findByPushTokenId(Long pushTokenId) {
        return userRepository.findByPushTokenId(pushTokenId);
    }

    @Transactional
    public PushToken unregistPushToken(Long id) {
        User user = findById(id);
        PushToken savedPushToken = user.getPushToken();
        user.setPushToken(null);
        return savedPushToken;
    }
}
