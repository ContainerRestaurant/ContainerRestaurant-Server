package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.contract.ContractService;
import container.restaurant.server.domain.contract.agree.ContractAgreeService;
import container.restaurant.server.web.dto.contract.ContractAgreeDto;
import container.restaurant.server.web.dto.contract.ContractInfoDto;
import container.restaurant.server.web.linker.ContractLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contract")
public class ContractController {
    private final ContractService contractService;
    private final ContractAgreeService contractAgreeService;
    private final ContractLinker contractLinker;

    @GetMapping
    public ResponseEntity<?> getContract(){
        CollectionModel<ContractInfoDto> contract = contractService.getContract();
        return ResponseEntity.ok(
                contract
                    .add(contractLinker.agreeContract().withRel("agree-contract"))
                    .add(contractLinker.getContract().withSelfRel())
        );
    }

    @PostMapping
    public ResponseEntity<?> agreeContract(
            @LoginUser SessionUser sessionUser,
            @RequestBody ContractAgreeDto contractAgreeDto
    ){
        contractAgreeService.agreeContract(sessionUser.getId(), contractAgreeDto.getAgreement());
        return ResponseEntity.noContent().build();
    }
}
