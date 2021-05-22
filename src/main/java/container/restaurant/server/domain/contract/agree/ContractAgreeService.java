package container.restaurant.server.domain.contract.agree;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class ContractAgreeService {
    private final ContractAgreeRepository contractAgreeRepository;

    private final UserService userService;

    @Transactional
    public void agreeContract(Long userId, Boolean agreement){
        User user = userService.findById(userId);
        ContractAgree contractAgree = contractAgreeRepository.findByUser(user);
        if(contractAgree == null)
            contractAgreeRepository.save(new ContractAgree(user, agreement));
        else
            contractAgree.setAgree(agreement);
    }
}
