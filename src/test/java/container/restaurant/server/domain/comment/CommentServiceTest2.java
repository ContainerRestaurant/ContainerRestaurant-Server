package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.web.base.BaseMockTest;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommentServiceTest2 extends BaseMockTest {

    @Mock CommentRepository commentRepository;
    @Mock UserService userService;
    @Mock FeedService feedService;
    @Mock ApplicationEventPublisher publisher;
    @Mock CommentLikeRepository commentLikeRepository;

    @InjectMocks CommentService commentService;

    @Captor
    ArgumentCaptor<Comment> commentCaptor;

    @Test
    @DisplayName("댓글 생성 테스트")
    public void createCommentTest() {
        //given 유저, 피드, 생성될 댓글의 ID 가 중어졌을 때
        long userId = 1L;
        String ownerNickname = "nickname";
        User user = spy(User.builder().nickname(ownerNickname).build());
        when(user.getId()).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(user);

        long feedId = 2L;
        Feed feed = spy(Feed.builder().build());
        when(feed.getId()).thenReturn(feedId);
        when(feedService.findById(feedId)).thenReturn(feed);

        long newCommentId = 3L;
        String commentContent = "comment";
        CommentCreateDto dto = new CommentCreateDto(commentContent, null);
        when(commentRepository.save(any(Comment.class))).thenAnswer(persistAnswer(newCommentId, Comment.class));

        //when 댓글 생성 서비스를 실행하면
        CommentInfoDto result = commentService.createComment(dto, feedId, userId);

        //then-1 피드 카운트가 오르고, 이벤트가 발행되며,
        verify(feed).commentCountUp();
        verify(publisher).publishEvent(any(Object.class));
        verify(commentRepository).save(commentCaptor.capture());

        //then-2 반환된 댓글 DTO, 저장된 댓글이 주어진 정보와 일치한다.
        Comment saved = commentCaptor.getValue();
        assertThat(saved.getContent()).isEqualTo(commentContent);
        assertThat(saved.getHasReply()).isFalse();
        assertThat(saved.getOwner().getId()).isEqualTo(userId);
        assertThat(saved.getFeed().getId()).isEqualTo(feedId);

        assertThat(result.getId()).isEqualTo(newCommentId);
        assertThat(result.getContent()).isEqualTo(commentContent);
        assertThat(result.getIsDeleted()).isEqualTo(false);
        assertThat(result.getOwnerId()).isEqualTo(userId);
        assertThat(result.getOwnerLevelTitle()).isEqualTo(user.getLevelTitle());
        assertThat(result.getOwnerNickName()).isEqualTo(ownerNickname);
    }

    @Test
    @DisplayName("단일 댓글 엔티티 조회 테스트")
    void findByIdTest() {
        //given 댓글 리포의 조회 입출력이 주어졌을 때
        Long targetId = 1L;
        Comment mockedComment = mock(Comment.class);
        when(commentRepository.findById(targetId)).thenReturn(of(mockedComment));

        //when 주어진 입력을 통해 댓글을 조회하면
        Comment result = commentService.findById(targetId);

        //then 출력으로 주어진 댓글이 반환된다.
        assertThat(result).isEqualTo(mockedComment);
    }

    @Test
    @DisplayName("단일 댓글 엔티티 조회 테스트 - 조회 결과 없음")
    void findByIdTest_noResult() {
        //given 조회 결과가 빈 입력이 주어졌을 때
        Long targetId = 1L;
        when(commentRepository.findById(targetId)).thenReturn(empty());

        //expect 주어진 입력을 통해 댓글을 조회하면 404 예외가 발생한다.
        assertThatThrownBy(() -> commentService.findById(targetId))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }

    // TODO 추가 구현이 필요하다.

    @NotNull
    private static <T extends BaseCreatedTimeEntity> Answer<Object> persistAnswer(Long id, Class<T> clazz) {
        return invocation -> {
            T argument = spy((T) invocation.getArgument(0));
            when(argument.getId()).thenReturn(id);
            when(argument.getCreatedDate()).thenReturn(LocalDateTime.now());
            return argument;
        };
    }

}