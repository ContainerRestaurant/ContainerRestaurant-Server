package container.restaurant.server.domain.feed.hit;

import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Entity(name = "TB_FEED_HIT")
public class FeedHit extends BaseTimeEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    private FeedHit(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }

    public static FeedHit of(User user, Feed feed) {
        return new FeedHit(user, feed);
    }

}
