package container.restaurant.server.domain.contract;

import container.restaurant.server.web.dto.contract.ContractInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ContractService {
    private final ContractRepository contractRepository;

    List<ContractInfoDto>  contractInfoDto = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 3 * * *")
    public void putContract(){
        contractRepository.findAll().forEach(contract ->
                contractInfoDto.add(ContractInfoDto.from(contract)));
    }

    @Transactional(readOnly = true)
    public CollectionModel<ContractInfoDto> getContract(){
        return CollectionModel.of(contractInfoDto);
    }
}
