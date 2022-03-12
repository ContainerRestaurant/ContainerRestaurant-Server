package container.restaurant.server.web.dto.contract;

import container.restaurant.server.domain.contract.Contract;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class ContractInfoDto extends RepresentationModel<ContractInfoDto> {
    private final String title;
    private final String article;

    public static ContractInfoDto from(Contract contract){ return new ContractInfoDto(contract); }

    public ContractInfoDto(Contract contract){
        this.title = contract.getTitle();
        this.article = contract.getArticle();
    }
}
