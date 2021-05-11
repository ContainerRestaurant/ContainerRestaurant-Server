package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;

@Getter
@NoArgsConstructor
@Entity(name = "TB_IMAGE_INFO")
public class Image extends BaseEntity {

    @NotEmpty
    @Column(nullable = false)
    private String url;

    @Builder
    public Image(String url) {
        this.url = url;
    }

}
