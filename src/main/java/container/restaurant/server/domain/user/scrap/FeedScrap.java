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
@Entity
public class FeedScrap extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    private FeedScrap(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }

    public static FeedScrap of(User user, Feed feed) {
        return new FeedScrap(user, feed);
    }

}
