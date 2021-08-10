package container.restaurant.server.web;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.report.ReportCommentRepository;
import container.restaurant.server.domain.report.ReportFeedRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ReportControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    ReportFeedRepository reportFeedRepository;

    @Autowired
    ReportCommentRepository reportCommentRepository;

    @Autowired
    CommentRepository commentRepository;

    @Override
    @AfterEach
    public void afterEach() {
        reportFeedRepository.deleteAll();
        reportCommentRepository.deleteAll();
        commentRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("피드 신고")
    void testReportFeed() throws Exception {
        for (int i = 0; i < 2; i++) {
            //when
            mvc.perform(
                    post("/api/report/feed/{feedId}", othersFeed.getId())
                            .session(myselfSession))
                    .andExpect(status().isNoContent())
                    .andDo(document("feed-report"));

            //then
            assertThat(
                    reportFeedRepository.existsDistinctByReporterIdAndFeedId(
                            myself.getId(), othersFeed.getId()))
                    .isTrue();
        }

    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("댓글 신고")
    void testReportComment() throws Exception {
        //given
        Comment comment = Comment.builder()
                .owner(other)
                .feed(othersFeed)
                .content("this is comment")
                .build();
        comment = commentRepository.save(comment);

        for (int i = 0; i < 2; i++) {
            //when
            mvc.perform(
                    post("/api/report/comment/{commentId}", comment.getId())
                            .session(myselfSession))
                    .andExpect(status().isNoContent())
                    .andDo(document("comment-report"));

            //then
            assertThat(
                    reportCommentRepository.existsDistinctByReporterIdAndCommentId(
                            myself.getId(), comment.getId()))
                    .isTrue();
        }
    }

}