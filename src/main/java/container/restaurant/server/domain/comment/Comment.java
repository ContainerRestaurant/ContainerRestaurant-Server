package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity(name = "TB_COMMENT")
public class Comment extends BaseCreatedTimeEntity {

    @NotNull
    @ManyToOne
    private User owner;

    @NotNull
    @ManyToOne
    private Feed feed;

    private String content;
    private Integer likeCount;

    @ManyToOne
    private Comment upperReply;
    private Boolean isDeleted;
    private Boolean isHaveReply;
    private Boolean isBlind;

    @Builder
    protected Comment(User owner, Feed feed, String content){
        this.owner = owner;
        this.feed = feed;
        this.content = content;
        this.likeCount = 0;
        this.upperReply = null;
        this.isDeleted = false;
        this.isHaveReply = false;
        this.isBlind = false;
    }

    protected Comment(CommentCreateDto commentCreateDto, Feed feed, User owner, Comment upperReply){
        this.owner = owner;
        this.feed = feed;
        this.content = commentCreateDto.getContent();
        this.likeCount = 0;
        this.upperReply = upperReply;
        this.isDeleted = false;
        this.isHaveReply = false;
        this.isBlind = false;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setIsHaveReply() { this.isHaveReply = true; }
}
