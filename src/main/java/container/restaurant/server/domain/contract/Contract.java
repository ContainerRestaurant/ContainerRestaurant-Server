package container.restaurant.server.domain.contract;

import container.restaurant.server.domain.base.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@Entity(name = "TB_CONTRACT")
public class Contract extends BaseTimeEntity {
    private Integer contractCode;
    private String title;
    private String article;


}
