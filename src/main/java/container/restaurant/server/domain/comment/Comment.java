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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY)
    private Comment upperReply;

    @OneToMany(fetch = LAZY, mappedBy = "upperReply")
    private List<Comment> replies = new ArrayList<>();

    private Boolean isDeleted;

    @Column(name = "is_have_reply")
    private Boolean hasReply;
    private Boolean isBlind;

    @Builder
    protected Comment(User owner, Feed feed, String content) {
        this.owner = owner;
        this.feed = feed;
        this.content = content;
        this.likeCount = 0;
        this.isDeleted = false;
        this.hasReply = false;
        this.isBlind = false;
    }

    public Comment setContent(String content){
        this.content = content;
        return this;
    }

    public void unsetHasReply() { this.hasReply = false; }

    public void setIsDeleted() { this.isDeleted = true; }

    public void likeCountUp() { this.likeCount++; }

    public void likeCountDown() { this.likeCount--; }

    public void isBelongTo(Comment upperReply) {
        this.upperReply = upperReply;

        upperReply.replies.add(this);
        upperReply.hasReply = true;
    }
}
