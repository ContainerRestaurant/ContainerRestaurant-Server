package container.restaurant.server.domain.contract.agree;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContractAgreeService {
    private final ContractAgreeRepository contractAgreeRepository;

    // TODO 약관 동의
}
