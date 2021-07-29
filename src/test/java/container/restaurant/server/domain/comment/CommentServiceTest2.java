package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.web.base.BaseMockTest;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

class CommentServiceTest2 extends BaseMockTest {

    @Mock CommentRepository commentRepository;
    @Mock UserService userService;
    @Mock FeedService feedService;
    @Mock ApplicationEventPublisher publisher;
    @Mock CommentLikeRepository commentLikeRepository;

    @InjectMocks CommentService commentService;

    User user;
    Feed feed;

    @BeforeEach
    void 유저_피드_세팅() {
        user = makeEntity(100L, () -> User.builder().build());
        feed = makeEntity(200L, () -> Feed.builder().build());
    }

    @Test
    @DisplayName("댓글 생성 테스트 - 일반 댓글")
    void 댓글_생성_테스트__일반_댓글() {
        //given 생성할 일반 댓글 엔티티 그리고 DTO 가 주어졌을 때
        Comment newComment = makeEntity(1L, () -> Comment.builder().build());
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        CommentCreateDto dto = mock(CommentCreateDto.class);
        when(dto.getUpperReplyId()).thenReturn(null);
        when(dto.toEntityWith(user, feed)).thenReturn(newComment);

        //when 댓글 생성 서비스를 실행하면
        Long result = commentService.createComment(dto, feed.getId(), user.getId());

        //then 주어진 댓글 ID 가 반환 / 피드 카운트 오름 / 이벤트 발행 / 주어진 새 댓글이 저장
        assertThat(result).isEqualTo(newComment.getId());
        verify(newComment, never()).isBelongTo(any(Comment.class));
        verify(feed).commentCountUp();
        verify(publisher).publishEvent(any(Object.class));
        verify(commentRepository).save(newComment);
    }

    @Test
    @DisplayName("댓글 생성 테스트 - 답댓글")
    void 댓글_생성_테스트__답댓글() {
        //given 상위 댓글, 생성할 답댓글 엔티티 그리고 DTO 가 주어졌을 때
        long upperCommentId = 1L;
        Comment upperComment = makeEntity(upperCommentId, () -> Comment.builder().build());

        Comment newReplyComment = makeEntity(2L, () -> Comment.builder().build());
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        CommentCreateDto dto = mock(CommentCreateDto.class);
        when(dto.getUpperReplyId()).thenReturn(upperCommentId);
        when(dto.toEntityWith(user, feed)).thenReturn(newReplyComment);

        //when 댓글 생성 서비스를 실행하면
        Long result = commentService.createComment(dto, feed.getId(), user.getId());

        //then 주어진 답댓글 ID 가 반환 / 상위 댓글 지정 / 피드 카운트 오름 / 이벤트 발행 / 주어진 새 답댓글이 저장
        assertThat(result).isEqualTo(newReplyComment.getId());
        verify(newReplyComment).isBelongTo(upperComment);
        verify(feed).commentCountUp();
        verify(publisher).publishEvent(any(Object.class));
        verify(commentRepository).save(newReplyComment);
    }

    @Test
    @DisplayName("단일 댓글 엔티티 조회 테스트")
    void 단일_댓글_엔티티_조회_테스트() {
        //given 댓글 리포의 조회 입출력이 주어졌을 때
        Comment mockedComment = makeEntity(1L, () -> Comment.builder().build());
        when(commentRepository.findById(mockedComment.getId())).thenReturn(of(mockedComment));

        //when 주어진 입력을 통해 댓글을 조회하면
        Comment result = commentService.findById(mockedComment.getId());

        //then 출력으로 주어진 댓글이 반환된다.
        assertThat(result).isEqualTo(mockedComment);
    }

    @Test
    @DisplayName("단일 댓글 엔티티 조회 테스트 - 조회 결과 없음")
    void 단일_댓글_엔티티_조회_테스트__조회_결과_없음() {
        //given 조회 결과가 빈 입력이 주어졌을 때
        Long targetId = 1L;
        when(commentRepository.findById(targetId)).thenReturn(empty());

        //expect 주어진 입력을 통해 댓글을 조회하면 404 예외가 발생한다.
        assertThatThrownBy(() -> commentService.findById(targetId))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("피드의 댓글 조회 테스트")
    void 피드의_댓글_조회_테스트() {
        //given 댓글, 답글이 있는 댓글과 그 답글과 좋아요 여부가 주어졌을 때
        long noReplyCommentId = 1L;
        Comment noReplyComment = makeEntity(noReplyCommentId,
                () -> Comment.builder().owner(user).feed(feed).build());

        Comment hasReplyComment = makeEntity(2L,
                () -> Comment.builder().owner(user).feed(feed).build());

        long replyCommentId = 3L;
        Comment replyComment = makeEntity(replyCommentId,
                () -> Comment.builder().owner(user).feed(feed).build());
        replyComment.isBelongTo(hasReplyComment);

        when(commentRepository.findFeedComments(feed.getId()))
                .thenReturn(List.of(noReplyComment, hasReplyComment));
        when(commentLikeRepository.findCommentIdsByFeedIdAndUserId(user.getId(), feed.getId()))
                .thenReturn(Set.of(noReplyCommentId, replyCommentId));

        //when 주어진 피드와 유저로 댓글을 조회하면
        Collection<CommentInfoDto> result = commentService.findAllByFeed(user.getId(), feed.getId()).getContent();

        //then 댓글 개수 만큼 조회되고, 답글이 확인되며, 좋아요 여부가 DTO 에 잘 적용되어있다.
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .anyMatch(dto -> dto.getId().equals(noReplyComment.getId()) && dto.getIsLike())
                .anyMatch(dto -> dto.getId().equals(hasReplyComment.getId()) && !dto.getIsLike() && null !=
                        assertThat(dto.getCommentReply())
                                .anyMatch(replyDto -> replyDto.getId().equals(replyComment.getId()) &&
                                        replyDto.getIsLike()));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 답글 없는 댓글")
    void 댓글_삭제_테스트__답글_없는_댓글() {
        //given 삭제할 답글 없는 댓글이 주어졌을 때
        Comment toDelete = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());
        Comment otherComment = makeEntity(2L, () -> Comment.builder().owner(user).feed(feed).build());

        //when 주어진 유저로 댓글을 삭제하면
        commentService.deleteById(toDelete.getId(), user.getId());

        //then 삭제한 댓글만 삭제되고, 댓글이 달린 피드의 댓글 수가 1 감소한다.
        verify(commentRepository).delete(toDelete);
        verify(feed, only()).commentCountDown();
        verify(commentRepository, never()).delete(otherComment);
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 답글이 있는 댓글")
    void 댓글_삭제_테스트__답글이_있는_댓글() {
        //given 삭제할 댓글과 그 댓글의 답글이 주어졌을 때
        Comment toDelete = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());
        Comment reply = makeEntity(2L, () -> Comment.builder().owner(user).feed(feed).build());
        reply.isBelongTo(toDelete);

        Comment otherComment = makeEntity(3L, () -> Comment.builder().owner(user).feed(feed).build());

        //when 주어진 유저로 댓글을 삭제하면
        commentService.deleteById(toDelete.getId(), user.getId());

        //then 삭제한 댓글이 삭제되지 않고 삭제마킹만 되며, 피드의 댓글 개수가 1 감소한다.
        verify(commentRepository, never()).delete(toDelete);
        verify(toDelete).delete();
        verify(feed, only()).commentCountDown();
        verify(commentRepository, never()).delete(otherComment);
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 댓글의 답글 중 하나")
    void 댓글_삭제_테스트__삭제된_댓글의_답글_중_하나() {
        //given 댓글과 삭제할 답글, 또 다른 답글이 주어졌을 때
        Comment comment = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());
        Comment toDelete = makeEntity(2L, () -> Comment.builder().owner(user).feed(feed).build());
        toDelete.isBelongTo(comment);
        Comment otherReply = makeEntity(3L, () -> Comment.builder().owner(user).feed(feed).build());
        otherReply.isBelongTo(comment);

        //when 주어진 유저로 삭제할 답글을 삭제하면
        commentService.deleteById(toDelete.getId(), user.getId());

        //then 삭제할 답글만 삭제되고, 피드의 댓글 개수가 1 감소한다.
        verify(commentRepository, never()).delete(comment);
        verify(commentRepository, never()).delete(otherReply);
        verify(commentRepository).delete(toDelete);
        verify(feed, only()).commentCountDown();
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 삭제되지 않은 댓글의 유일 답글")
    void 댓글_삭제_테스트__댓글이_삭제되지_않은_답글() {
        //given 댓글과 삭제할 답글이 주어졌을 때
        Comment comment = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());
        Comment toDelete = makeEntity(2L, () -> Comment.builder().owner(user).feed(feed).build());
        toDelete.isBelongTo(comment);

        //when 주어진 유저로 답글을 삭제하면
        commentService.deleteById(toDelete.getId(), user.getId());

        //then 답글이 삭제되고 댓글은 삭제되지 않으며, 피드 개수가 1 감소한다.
        verify(commentRepository, never()).delete(comment);
        verify(commentRepository).delete(toDelete);
        verify(feed, only()).commentCountDown();
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 삭제된 댓글의 유일 답글")
    void 댓글_삭제_테스트__삭제된_댓글의_유일_답글() {
        //given 삭제된 댓글과 삭제할 답글이 주어졌을 때
        Comment comment = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());
        comment.delete();
        Comment toDelete = makeEntity(2L, () -> Comment.builder().owner(user).feed(feed).build());
        toDelete.isBelongTo(comment);

        //when 주어진 유저로 답글을 삭제하면
        commentService.deleteById(toDelete.getId(), user.getId());

        //then 삭제된 댓글과 답글 모두 삭제되고, 피드 개수가 1 감소한다.
        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(toDelete);
        verify(feed, only()).commentCountDown();
    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 댓글 작성자가 아닌 경우")
    void 댓글_삭제_테스트__댓글_작성자가_아닌_경우() {
        //given 삭제할 답글 없는 댓글이 주어졌을 때
        Comment toDelete = makeEntity(1L, () -> Comment.builder().owner(user).feed(feed).build());

        //expect 다른 유저로 댓글을 삭제하면 403 예외가 발생한다.
        assertThatThrownBy(() -> commentService.deleteById(toDelete.getId(), null))
                .isInstanceOf(FailedAuthorizationException.class);
    }

    // TODO 추가 구현이 필요하다.

    @NotNull
    private <T extends BaseEntity> T makeEntity(long id, Supplier<T> supplier) {
        T entity = spy(supplier.get());

        // Base 클래스의 속성을 세팅
        when(entity.getId()).thenReturn(id);
        if (entity instanceof BaseCreatedTimeEntity)
            when(((BaseCreatedTimeEntity) entity).getCreatedDate()).thenReturn(now());
        if (entity instanceof BaseTimeEntity) {
            when(((BaseTimeEntity) entity).getModifiedDate()).thenReturn(now());
        }

        // 리포지토리 모킹
        if (entity instanceof  User) {
            when(userService.findById(id)).thenReturn((User) entity);
        } else if (entity instanceof Feed) {
            when(feedService.findById(id)).thenReturn((Feed) entity);
        } else if (entity instanceof Comment) {
            when(commentRepository.findById(id)).thenReturn(of((Comment) entity));
        }
        return entity;
    }
}