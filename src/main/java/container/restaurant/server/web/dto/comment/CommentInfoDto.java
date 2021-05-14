package container.restaurant.server.web.dto.comment;

import container.restaurant.server.domain.comment.Comment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CommentInfoDto extends RepresentationModel<CommentInfoDto> {
    private final Long id;
    private final String content;
    private final Integer likeCount;
    private final Long ownerId;
    private final String ownerNickName;
    private final String ownerProfile;
    private final Integer ownerLevel;
    private final String createdDate;
    private final List<CommentInfoDto> commentReply = new ArrayList<>();

    public static CommentInfoDto from(Comment comment){
        return new CommentInfoDto(comment);
    }

    protected CommentInfoDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.ownerId = comment.getOwner().getId();
        this.ownerNickName = comment.getOwner().getNickname();
        this.ownerProfile = comment.getOwner().getProfile();
        this.ownerLevel = comment.getOwner().getLevel();
        this.createdDate = comment.getCreatedDate().format(DateTimeFormatter.ofPattern("MM.dd"));
    }
}
