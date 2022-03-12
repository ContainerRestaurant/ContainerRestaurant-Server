package container.restaurant.server.domain.report;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Feed")
public class ReportFeed extends Report {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    private ReportFeed(User user, Feed feed) {
        super(user);
        this.feed = feed;
    }

    public static ReportFeed of(User user, Feed feed) {
        return new ReportFeed(user, feed);
    }

}
