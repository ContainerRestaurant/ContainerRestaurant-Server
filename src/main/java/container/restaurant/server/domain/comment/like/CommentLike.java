package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "TB_COMMENT_LIKE")
public class CommentLike extends BaseEntity {

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private Comment comment;

    public static CommentLike of(User user, Comment comment) {
        return new CommentLike(user, comment);
    }

}
