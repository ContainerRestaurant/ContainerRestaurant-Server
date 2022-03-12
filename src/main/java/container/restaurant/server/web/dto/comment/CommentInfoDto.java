package container.restaurant.server.web.dto.comment;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.utils.ImageUtils;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Boolean.FALSE;

@Getter
public class CommentInfoDto extends RepresentationModel<CommentInfoDto> {
    private final Long id;
    private final String content;
    private final Boolean isDeleted;
    private final Integer likeCount;
    private final Long ownerId;
    private final String ownerNickName;
    private final String ownerProfile;
    private final String ownerLevelTitle;
    private final String createdDate;
    private final Boolean isLike;
    private final List<CommentInfoDto> commentReply = new ArrayList<>();

    public static CommentInfoDto from(Comment comment){
        return from(comment, FALSE);
    }

    public static CommentInfoDto from(Comment comment, Boolean isLike){
        if(comment.getIsDeleted())
            return new CommentInfoDto(comment.getId());
        return new CommentInfoDto(comment, isLike);
    }

    protected CommentInfoDto(Comment comment, Boolean isLike){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.isDeleted = false;
        this.likeCount = comment.getLikeCount();
        this.ownerId = comment.getOwner().getId();
        this.ownerNickName = comment.getOwner().getNickname();
        this.ownerProfile = ImageUtils.getUrlFromImage(comment.getOwner().getProfile());
        this.ownerLevelTitle = comment.getOwner().getLevelTitle();
        this.createdDate = comment.getCreatedDate().format(DateTimeFormatter.ofPattern("MM.dd"));
        this.isLike = isLike;
    }

    protected CommentInfoDto(Long id){
        this.id = id;
        this.content = null;
        this.isDeleted = true;
        this.likeCount = null;
        this.ownerId = null;
        this.ownerNickName = null;
        this.ownerProfile = null;
        this.ownerLevelTitle = null;
        this.createdDate = null;
        this.isLike = null;
    }

    public void addCommentReply(CommentInfoDto commentInfoDto){
        commentReply.add(commentInfoDto);
    }

    public void ifHasReply(Consumer<CommentInfoDto> consumer) {
        if (commentReply.size() == 0) return;
        commentReply.forEach(consumer);
    }
}
