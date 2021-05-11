package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@Entity(name = "TB_IMAGE_INFO")
public class Image extends BaseEntity {

    @Null
    @ManyToOne(optional = false)
    private Feed feed;

    @NotEmpty
    @Column(nullable = false)
    private String url;

    @Builder
    public Image(Feed feed, String url) {
        this.feed = feed;
        this.url = url;
    }

}
