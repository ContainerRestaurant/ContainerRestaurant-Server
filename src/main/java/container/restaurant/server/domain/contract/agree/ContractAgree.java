package container.restaurant.server.domain.contract.agree;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity(name = "TB_CONTRACT_AGREE")
public class ContractAgree extends BaseEntity {

    @NotNull
    @OneToOne
    private User user;
    private Boolean isAgree;
    @CreatedDate
    private LocalDateTime agreeDate;


}
