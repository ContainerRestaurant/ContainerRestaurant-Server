package container.restaurant.server.domain.report;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Comment")
public class ReportComment extends Report {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    private ReportComment(User user, Comment comment) {
        super(user);
        this.comment = comment;
    }

    public static ReportComment of(User user, Comment comment) {
        return new ReportComment(user, comment);
    }
}
