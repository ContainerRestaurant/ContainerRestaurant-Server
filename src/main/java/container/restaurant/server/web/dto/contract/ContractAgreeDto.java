package container.restaurant.server.web.dto.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@NoArgsConstructor
public class ContractAgreeDto extends RepresentationModel<ContractAgreeDto> {
    private boolean agreement;

    public ContractAgreeDto(boolean agreement){
        this.agreement = agreement;
    }

    public boolean getAgreement(){ return agreement; }
}
