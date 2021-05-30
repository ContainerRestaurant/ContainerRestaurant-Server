package container.restaurant.server.domain.home.phrase;

import container.restaurant.server.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_PHRASE")
public class Phrase extends BaseEntity {

    private String phrase;

}
