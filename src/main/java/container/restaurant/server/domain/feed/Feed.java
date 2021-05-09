package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Entity(name = "TB_FEED")
public class Feed extends BaseTimeEntity {

    @NotNull
    @ManyToOne
    private User owner;

    @NotNull
    @ManyToOne
    private Restaurant restaurant;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    private String thumbnailUrl;

    @Size(max = 500)
    private String content;

    private Boolean welcome;

    @NotNull
    @Min(1) @Max(5)
    private Integer difficulty;

    private Integer likeCount;

    private Integer scrapedCount;

    private Integer replyCount;

    private Integer hits;

    private Boolean isBlind;

    private Boolean isDeleted;

    @Builder
    protected Feed(
            User owner, Restaurant restaurant, Category category,
            String thumbnailUrl, String content, Boolean welcome, Integer difficulty
    ) {
        this.owner = owner;
        this.restaurant = restaurant;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.content = content;
        this.welcome = welcome != null ? welcome : false;
        this.difficulty = difficulty;
        this.likeCount = 0;
        this.scrapedCount = 0;
        this.replyCount = 0;
        this.hits = 0;
        this.isBlind = false;
        this.isDeleted = false;
    }

}