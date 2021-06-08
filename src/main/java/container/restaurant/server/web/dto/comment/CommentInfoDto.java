package container.restaurant.server.web.dto.comment;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.feed.picture.ImageService;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class CommentInfoDto extends RepresentationModel<CommentInfoDto> {
    private final Long id;
    private final String content;
    private final Boolean isDeleted;
    private final Integer likeCount;
    private final Long ownerId;
    private final String ownerNickName;
    private final String ownerProfile;
    private final Integer ownerLevel;
    private final String createdDate;
    private final List<CommentInfoDto> commentReply = new ArrayList<>();

    public static CommentInfoDto from(Comment comment){
        if(comment.getIsDeleted())
            return new CommentInfoDto(comment.getId());
        return new CommentInfoDto(comment);
    }

    protected CommentInfoDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.isDeleted = false;
        this.likeCount = comment.getLikeCount();
        this.ownerId = comment.getOwner().getId();
        this.ownerNickName = comment.getOwner().getNickname();
        this.ownerProfile = ImageService.getUrlFromImage(comment.getOwner().getProfile());
        this.ownerLevel = comment.getOwner().getLevel();
        this.createdDate = comment.getCreatedDate().format(DateTimeFormatter.ofPattern("MM.dd"));
    }

    protected CommentInfoDto(Long id){
        this.id = id;
        this.content = null;
        this.isDeleted = true;
        this.likeCount = null;
        this.ownerId = null;
        this.ownerNickName = null;
        this.ownerProfile = null;
        this.ownerLevel = null;
        this.createdDate = null;
    }

    public void addCommentReply(CommentInfoDto commentInfoDto){
        commentReply.add(commentInfoDto);
    }

    public void ifHasReply(Consumer<CommentInfoDto> consumer) {
        if (commentReply.size() == 0) return;
        commentReply.forEach(consumer);
    }
}
