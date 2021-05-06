package container.restaurant.server.domain.report;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Comment")
public class ReportComment extends Report {

    @NotNull
    @ManyToOne
    private Comment comment;

    private ReportComment(User user, Comment comment, String description) {
        super(user, description);
        this.comment = comment;
    }

    public static ReportComment of(User user, Comment comment, String description) {
        return new ReportComment(user, comment, description);
    }
}
