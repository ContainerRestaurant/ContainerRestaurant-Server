package container.restaurant.server.web.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@NoArgsConstructor
public class CommentCreateDto extends RepresentationModel<CommentCreateDto> {
    private String content;
    private Long upperReplyId;

    public CommentCreateDto(String content, Long upperReplyId){
        this.content = content;
        this.upperReplyId = upperReplyId;
    }

    public CommentCreateDto(String content){
        this.content = content;
    }
}