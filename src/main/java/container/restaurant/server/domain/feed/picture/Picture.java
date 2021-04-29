package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
public class Picture extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false)
    private Feed feed;

    // TODO Restaurant

    @URL
    @NotEmpty
    @Column(nullable = false)
    private String url;

    // TODO Restaurant
    @Builder
    public Picture(Feed feed, String url) {
        this.feed = feed;
        this.url = url;
    }

}
