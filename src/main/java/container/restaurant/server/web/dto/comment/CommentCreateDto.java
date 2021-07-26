package container.restaurant.server.web.dto.comment;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateDto {
    private String content;
    private Long upperReplyId;

    public CommentCreateDto(String content, Long upperReplyId){
        this.content = content;
        this.upperReplyId = upperReplyId;
    }

    public CommentCreateDto(String content){
        this.content = content;
    }

    public Comment toEntityWith(User owner, Feed feed) {
        return Comment.builder()
                .owner(owner)
                .feed(feed)
                .content(this.content)
                .build();
    }
}