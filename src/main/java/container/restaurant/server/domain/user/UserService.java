package container.restaurant.server.domain.user;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.user.NicknameExistsDto;
import container.restaurant.server.web.dto.user.UserInfoDto;
import container.restaurant.server.web.dto.user.UserInfoDtoAssembler;
import container.restaurant.server.web.dto.user.UserUpdateDto;
import container.restaurant.server.web.linker.UserControllerLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserControllerLinker userLinker;

    private final UserInfoDtoAssembler userInfoDtoAssembler;

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 사용자입니다.(id:" + id + ")"));
    }

    @Transactional
    public User createOrUpdateByEmail(
            @Email String email,
            Supplier<@Valid ? extends User> supplier
    ) {
        User user = userRepository.findByEmail(email)
                .orElse(supplier.get());
        return userRepository.save(user);
    }

    public UserInfoDto findById(
            Long id, Boolean auth
    ) throws ResourceNotFoundException {
        return userInfoDtoAssembler.toModel(getUser(id), auth);
    }

    @Transactional
    public UserInfoDto update(Long id, UserUpdateDto updateDto) {
        User user = getUser(id);
        updateDto.updateUser(user);
        return userInfoDtoAssembler.toModel(user, true);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public NicknameExistsDto existsUserByNickname(String nickname) {
        return NicknameExistsDto.of(nickname, userRepository.existsUserByNickname(nickname))
                .add(userLinker.existsNickname(nickname).withSelfRel());
    }
}
