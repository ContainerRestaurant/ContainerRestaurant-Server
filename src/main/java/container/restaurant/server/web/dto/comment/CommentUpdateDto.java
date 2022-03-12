package container.restaurant.server.web.dto.comment;

import container.restaurant.server.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateDto {
    private String content;

    @Builder
    public CommentUpdateDto(String content){
        this.content = content;
    }

    public void updateComment(Comment comment){
        if(content != null)
            comment.setContent(content);
    }
}
