package container.restaurant.server.web;

import container.restaurant.server.domain.contract.ContractService;
import container.restaurant.server.web.dto.contract.ContractInfoDto;
import container.restaurant.server.web.linker.ContractLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contract")
public class ContractController {
    private final ContractService contractService;
    private final ContractLinker contractLinker;

    @GetMapping
    public ResponseEntity<?> getContract(){
        CollectionModel<ContractInfoDto> contract = contractService.getContract();
        return ResponseEntity.ok(
                contract
                .add(contractLinker.getContract().withSelfRel())
        );
    }
}
