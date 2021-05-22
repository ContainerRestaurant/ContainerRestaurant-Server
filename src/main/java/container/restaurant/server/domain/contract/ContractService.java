package container.restaurant.server.domain.contract;

import container.restaurant.server.web.dto.contract.ContractInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ContractService {
    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public CollectionModel<ContractInfoDto> getContract(){
        List<ContractInfoDto> contractInfoDtos = new ArrayList<>();
        contractRepository.findAll().forEach(contract ->
            contractInfoDtos.add(ContractInfoDto.from(contract)));
        return CollectionModel.of(contractInfoDtos);
    }
}
