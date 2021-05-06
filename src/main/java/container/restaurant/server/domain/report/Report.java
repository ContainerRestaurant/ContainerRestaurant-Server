package container.restaurant.server.domain.report;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "TB_REPORT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("not null")
public class Report extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne
    protected User reporter;

    protected String description;

}
