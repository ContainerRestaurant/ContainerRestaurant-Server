package container.restaurant.server.domain.user;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.user.UserInfoDto;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createOrUpdateByEmail(
            @Email String email,
            Supplier<@Valid ? extends User> supplier
    ) {
        User user = userRepository.findByEmail(email)
                .orElse(supplier.get());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserInfoDto getUserInfoById(Long id) throws ResourceNotFoundException {

        return UserInfoDto.from(findById(id));
    }

    @Transactional
    public UserInfoDto update(Long id, UserUpdateDto updateDto) {
        User user = findById(id);
        updateDto.updateUser(user);
        return UserInfoDto.from(user);
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
        return userRepository.findByToDayFeedWriter(to,from);
    }
}
