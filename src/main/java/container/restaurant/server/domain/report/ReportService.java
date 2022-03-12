package container.restaurant.server.domain.report;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportFeedRepository reportFeedRepository;
    private final ReportCommentRepository reportCommentRepository;

    private final UserService userService;
    private final FeedService feedService;
    private final CommentService commentService;

    @Transactional
    public void reportFeed(Long reporterId, Long feedId) {
        if (reportFeedRepository.existsDistinctByReporterIdAndFeedId(reporterId, feedId))
            return;

        User reporter = userService.findById(reporterId);
        Feed feed = feedService.findById(feedId);
        reportFeedRepository.save(ReportFeed.of(reporter, feed));
    }

    @Transactional
    public void reportComment(Long reporterId, Long commentId) {
        if (reportCommentRepository.existsDistinctByReporterIdAndCommentId(reporterId, commentId))
            return;

        User reporter = userService.findById(reporterId);
        Comment comment = commentService.findById(commentId);
        reportCommentRepository.save(ReportComment.of(reporter, comment));
    }

    @Transactional
    public void deleteAllByReporterId(Long reporterId){
        reportCommentRepository.deleteAllByReporterId(reporterId);
        reportFeedRepository.deleteAllByReporterId(reporterId);
    }
}
