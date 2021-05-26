package container.restaurant.server.domain.push.event;

import container.restaurant.server.domain.comment.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedCommentedEvent {
    private final Comment comment;
}
