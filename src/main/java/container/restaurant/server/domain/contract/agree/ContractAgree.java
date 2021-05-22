package container.restaurant.server.domain.contract.agree;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity(name = "TB_CONTRACT_AGREE")
public class ContractAgree extends BaseCreatedTimeEntity {

    @NotNull
    @OneToOne
    private User user;
    private Boolean isAgree;

    protected ContractAgree(User user, Boolean isAgree){
        this.user = user;
        this.isAgree = isAgree;
    }

    public void setAgree(Boolean agree) {
        this.isAgree = agree;
    }
}
