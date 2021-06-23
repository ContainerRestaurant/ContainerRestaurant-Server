package container.restaurant.server.web;

import container.restaurant.server.domain.comment.like.CommentLike;
import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.web.base.BaseFeedAndCommentControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CommentLikeControllerTest extends BaseFeedAndCommentControllerTest {

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Test
    @DisplayName("댓글 좋아요 테스트")
    void userLikeComment() throws Exception {

        ResultActions perform = mvc.perform(
                post("/api/like/comment/{commentId}", myFeedComment.getId())
                        .session(myselfSession));

        perform
                .andExpect(status().isOk())
                .andDo(document("comment-like",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("cancel-like").description("본 좋아요를 취소하는 링크")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 테스트")
    void userCancelLikeComment() throws Exception {
        commentLikeRepository.save(CommentLike.of(myself, myFeedComment));

        ResultActions perform = mvc.perform(
                delete("/api/like/comment/{commentId}", myFeedComment.getId())
                        .session(myselfSession));

        perform
                .andExpect(status().isOk())
                .andDo(document("comment-like-cancel",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("like").description("다시 좋아요 하는 링크")
                        )
                ));
    }
}