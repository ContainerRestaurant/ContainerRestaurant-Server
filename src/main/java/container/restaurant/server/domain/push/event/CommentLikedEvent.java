package container.restaurant.server.domain.push.event;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentLikedEvent {
    private final User from;
    private final Comment comment;
}
