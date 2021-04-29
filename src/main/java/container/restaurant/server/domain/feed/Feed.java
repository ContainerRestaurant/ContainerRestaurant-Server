package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.feed.picture.Picture;
import container.restaurant.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Entity
public class Feed extends BaseTimeEntity {

    @NotNull
    @ManyToOne
    private User owner;

    // TODO Restaurant

    @OneToOne
    private Picture thumbnail;

    @Size(max = 500)
    private String description;

    private Boolean welcome;

    @NotNull
    @Min(1) @Max(5)
    private Integer difficulty;

    private Integer likeCount;

    private Integer scrapedCount;

    private Integer replyCount;

    private Boolean isBlind;

    private Boolean isDeleted;

    // TODO Restaurant
    @Builder
    protected Feed(User owner, Picture thumbnail, String description, Boolean welcome, Integer difficulty) {
        this.owner = owner;
        this.thumbnail = thumbnail;
        this.description = description;
        this.welcome = welcome != null ? welcome : false;
        this.difficulty = difficulty;
        this.likeCount = 0;
        this.scrapedCount = 0;
        this.replyCount = 0;
        this.isBlind = false;
        this.isDeleted = false;
    }

}
