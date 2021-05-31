package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "TB_FEED")
public class Feed extends BaseTimeEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
    private List<Container> containerList;

    private Long thumbnailImageId;

    @Size(max = 500)
    private String content;

    private Boolean welcome;

    @NotNull
    @Min(1) @Max(5)
    private Integer difficulty;

    private Integer likeCount;

    private Integer scrapCount;

    private Integer replyCount;

    private Integer hitCount;

    private Boolean isBlind;

    private Boolean isDeleted;

    @Getter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
    private List<ScrapFeed> scrapedBy;

    @Builder
    protected Feed(
            User owner, Restaurant restaurant, Category category, List<Container> menus,
            Long thumbnailImageId, String content, Boolean welcome, Integer difficulty
    ) {
        this.owner = owner;
        this.restaurant = restaurant;
        this.thumbnailImageId = thumbnailImageId;
        this.category = category;
        this.content = content;
        this.welcome = welcome != null ? welcome : false;
        this.difficulty = difficulty;
        this.likeCount = 0;
        this.scrapCount = 0;
        this.replyCount = 0;
        this.hitCount = 0;
        this.isBlind = false;
        this.isDeleted = false;

        this.containerList = new ArrayList<>();
        if (menus != null) {
            this.containerList.addAll(menus);
        }
    }

    public void likeCountUp() {
        this.likeCount++;
    }

    public void likeCountDown() {
        this.likeCount--;
    }

    public void scrapCountUp() {
        this.scrapCount++;
    }

    public void scrapCountDown() {
        this.scrapCount--;
    }

    public void commentCountUp() {
        this.replyCount++;
    }

    public void commentCountDown() {
        this.replyCount--;
    }

    public void hit() {
        this.hitCount++;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setThumbnailImageId(Long thumbnailImageId) {
        this.thumbnailImageId = thumbnailImageId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWelcome(Boolean welcome) {
        this.welcome = welcome;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public void setContainerList(List<Container> containerList) {
        this.containerList = containerList;
    }

    public Integer getRecommendScore() {
        return (likeCount * 5) + hitCount;
    }

    public final static Comparator<Feed> RECOMMEND_COMPARATOR = (Feed f1, Feed f2) -> {
        int dif = f1.getRecommendScore() - f2.getRecommendScore();
        if (dif != 0) {
            return dif;
        } else if (f1.getCreatedDate().equals(f2.getCreatedDate())) {
            return 0;
        } else {
            return f1.getCreatedDate().isBefore(f2.getCreatedDate()) ? -1 : 1;
        }
    };
}
