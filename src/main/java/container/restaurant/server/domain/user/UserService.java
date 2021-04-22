package container.restaurant.server.domain.user;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoDto findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .map(UserInfoDto::from)
                .orElseThrow(() ->
                        new ResourceNotFoundException("존재하지 않는 사용자입니다.(id:" + id + ")"));
    }
}
