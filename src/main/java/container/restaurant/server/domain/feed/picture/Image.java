package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@Entity(name = "TB_IMAGE_INFO")
public class Image extends BaseEntity {

    public static Image from(String imageKey) {
        return new Image("/api/image/" + imageKey);
    }

    // 실제로는... uri 를 갖는다...
    @NotEmpty
    @Column(nullable = false)
    private String url;

    @Builder
    public Image(String url) {
        this.url = url;
    }

    public String getKey() {
        return url.substring(url.lastIndexOf('/') + 1);
    }

}
