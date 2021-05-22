package container.restaurant.server.domain.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContractService {
    private final ContractRepository contractRepository;

    // TODO 약관 내역 보기
}
