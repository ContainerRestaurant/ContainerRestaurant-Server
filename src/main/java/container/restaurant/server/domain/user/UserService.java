package container.restaurant.server.domain.user;

import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.process.oauth.OAuthAgentFactory;
import container.restaurant.server.web.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final ImageService imageService;

    private final OAuthAgentFactory authAgentFactory;

    @Transactional
    public User createOrUpdate(
            AuthProvider provider, String authId, Supplier<@Valid ? extends User> supplier
    ) {
        User user = userRepository.findByAuthProviderAndAuthId(provider, authId)
                .orElseGet(supplier);

        return userRepository.save(user);
    }

    @Transactional
    public UserDto.Info createFrom(UserDto.Create dto) {
        OAuthAttributes attrs = authAgentFactory.get(dto.getProvider())
                .getAuthAttrFrom(dto.getAccessToken())
                .orElseThrow(() -> new ValidationException("유효하지 않은 액세스토큰입니다."));

        User newUser = of(attrs.toEntity())
                .flatMap(user -> userRepository.findByAuthProviderAndAuthId(user.getAuthProvider(), user.getAuthId()))
                .orElseGet(() -> userRepository.save(attrs.toEntity()));

        ofNullable(dto.getProfileId())
                .ifPresent(profileId -> newUser.setProfile(imageService.findById(profileId)));

        return UserDto.Info.from(newUser);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto.Info> tokenLogin(UserDto.TokenLogin dto) {
        return authAgentFactory.get(dto.getProvider())
                .getAuthAttrFrom(dto.getAccessToken())
                .flatMap(attributes -> userRepository
                        .findByAuthProviderAndAuthId(attributes.getProvider(), attributes.getAuthId())
                        .map(UserDto.Info::from));
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
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Boolean existsUserByNickname(String nickname) {
        return userRepository.existsUserByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public List<User> findByToDayFeedWriters(LocalDateTime to, LocalDateTime from) {
        return userRepository.findByToDayFeedWriter(to, from);
    }

    @Transactional(readOnly = true)
    public List<User> findByFeedCountTopUsers(LocalDateTime to, LocalDateTime from) {
        return userRepository.findByFeedCountTopUsers(to, from);
    }

    @Transactional(readOnly = true)
    public User findByPushTokenId(Long pushTokenId) {
        return userRepository.findByPushTokenId(pushTokenId);
    }
}
