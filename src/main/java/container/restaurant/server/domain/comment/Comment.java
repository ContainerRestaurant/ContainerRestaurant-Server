package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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

    @Column(name = "is_have_reply")
    private Boolean hasReply;
    private Boolean isBlind;

    @Builder
    protected Comment(User owner, Feed feed, String content, Comment upperReply) {
        this.owner = owner;
        this.feed = feed;
        this.content = content;
        this.likeCount = 0;
        this.upperReply = upperReply;
        if (upperReply != null) {
            upperReply.setHasReply();
        }
        this.isDeleted = false;
        this.hasReply = false;
        this.isBlind = false;
    }

    public Comment setContent(String content){
        this.content = content;
        return this;
    }

    public void setHasReply() {
        this.hasReply = true;
    }

    public void unsetHasReply() { this.hasReply = false; }

    public void setIsDeleted() { this.isDeleted = true; }

    public void likeCountUp() { this.likeCount++; }

    public void likeCountDown() { this.likeCount--; }
}
