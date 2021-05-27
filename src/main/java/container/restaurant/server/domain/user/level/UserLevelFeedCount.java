package container.restaurant.server.domain.user.level;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "TB_USER_LEVEL_FEED_COUNT")
public class UserLevelFeedCount extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    private Integer count;
    private LocalDate date;

    public static UserLevelFeedCount from(Feed feed) {
        return new UserLevelFeedCount(feed.getOwner(), 0, feed.getCreatedDate().toLocalDate());
    }

    public int countAggregate(int count) {
        return this.count += count;
    }
}
