package container.restaurant.server.domain.push.event;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedLikedEvent {
    private final User from;
    private final Feed feed;
}
