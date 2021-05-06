package container.restaurant.server.domain.user.scrap;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity(name = "TB_SCRAP_FEED")
public class ScrapFeed extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    private ScrapFeed(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }

    public static ScrapFeed of(User user, Feed feed) {
        return new ScrapFeed(user, feed);
    }

}
