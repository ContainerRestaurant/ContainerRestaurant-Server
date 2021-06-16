package container.restaurant.server.web;

import container.restaurant.server.web.base.BaseFeedAndCommentControllerTest;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CommentControllerTest extends BaseFeedAndCommentControllerTest {
    @Test
    @DisplayName("댓글 쓰기 테스트")
    void createComment() throws Exception{
        //given
        CommentCreateDto dto = new CommentCreateDto("테스트 댓글", myFeedComment.getId());

        mvc.perform(
                post("/api/comment/feed/{feedId}", myFeed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .session(myselfSession))
                .andExpect(status().isOk())
                .andDo(document("comment-create",
                        requestFields(
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("upperReplyId").description("대댓글을 쓰는 경우, 상위 댓글 ID +\n" +
                                        "(대댓글이 아니라면 생략 가능)")
                        )));
    }

    @Test
    @DisplayName("특정 피드의 댓글 가져오기")
    void getCommentByFeed() throws Exception {

        mvc.perform(get("/api/comment/feed/{feedId}", myFeed.getId()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("feed-comments",
                        responseFields(
                                subsectionWithPath("_embedded.commentInfoDtoList[]").description("해당 피드의 댓글 목록"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 목록")
                        )
                ))
                .andDo(document("comment-info",
                        responseFields(beneathPath("_embedded.commentInfoDtoList"),
                                fieldWithPath("id").description("본 댓글 식별 ID"),
                                fieldWithPath("content").description("본 댓글 내용"),
                                fieldWithPath("isDeleted").description("댓글의 삭제 유무 +\n(삭제 된 경우 다른 속성들이 null"),
                                fieldWithPath("likeCount").description("본 댓글 좋아요 횟수"),
                                fieldWithPath("ownerId").description("본 댓글 작성자 식별 ID"),
                                fieldWithPath("ownerNickName").description("본 댓글 작성자의 닉네임"),
                                fieldWithPath("ownerProfile").description("본 댓글 작성자 프로필 링크"),
                                fieldWithPath("ownerLevelTitle").description("본 댓글 작성자 레벨 타이틀"),
                                fieldWithPath("createdDate").description("본 댓글 작성 일시"),
                                subsectionWithPath("commentReply").description("본 댓글의 답댓글 목록"),
                                subsectionWithPath("_links").description("본 댓글에서 전이 가능한 링크 목록")
                        )
                        //TODO 댓글 링크 Docs 추가 #98 해결되면 추가 구현
                ));
    }

    @Test
    @DisplayName("댓글 업데이트")
    void updateCommentById() throws Exception {
        //given
        CommentUpdateDto dto = new CommentUpdateDto("변경된 댓글");

        mvc.perform(
                patch("/api/comment/{commentId}", myFeedCommentReply.getId())
                        .session(myselfSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(document("comment-update"));
    }

    //TODO 댓글 삭제 버그 #98 해결되면 추가 구현
//    @Test
//    @DisplayName("댓글 삭제")
//    void deleteCommentById() throws Exception {
//
//        mvc.perform(
//                delete("/api/comment/{commentId}", myFeedCommentReply.getId())
//                        .session(myselfSession))
//                .andExpect(status().isNoContent())
//                .andDo(document("comment-delete"));
//    }

}