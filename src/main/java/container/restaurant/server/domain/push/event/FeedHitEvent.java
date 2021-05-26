package container.restaurant.server.domain.push.event;

import container.restaurant.server.domain.feed.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedHitEvent {
    private final Feed feed;
    private String msg = "당신의 용기가 세상에 영향을 주고있어요\uD83D\uDCAB";
}
