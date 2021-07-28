package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.base.BaseTimeEntity;
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

    @Test
    @DisplayName("댓글 생성 테스트 - 일반 댓글")
    public void 댓글_생성_테스트__일반_댓글() {
        //given 유저, 피드, 생성할 일반 댓글 엔티티 그리고 DTO 가 주어졌을 때
        long userId = 1L;
        User user = makeEntity(userId, () -> User.builder().build());

        long feedId = 2L;
        Feed feed = makeEntity(feedId, () -> Feed.builder().build());

        long newCommentId = 3L;
        Comment newComment = makeEntity(newCommentId, () -> Comment.builder().build());
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        CommentCreateDto dto = mock(CommentCreateDto.class);
        when(dto.getUpperReplyId()).thenReturn(null);
        when(dto.toEntityWith(user, feed)).thenReturn(newComment);

        //when 댓글 생성 서비스를 실행하면
        Long result = commentService.createComment(dto, feedId, userId);

        //then 주어진 댓글 ID 가 반환 / 피드 카운트 오름 / 이벤트 발행 / 주어진 새 댓글이 저장
        assertThat(result).isEqualTo(newCommentId);
        verify(newComment, never()).isBelongTo(any(Comment.class));
        verify(feed).commentCountUp();
        verify(publisher).publishEvent(any(Object.class));
        verify(commentRepository).save(newComment);
    }

    @Test
    @DisplayName("댓글 생성 테스트 - 답댓글")
    void 댓글_생성_테스트__답댓글() {
        //given 유저, 피드, 상위 댓글, 생성할 답댓글 엔티티 그리고 DTO 가 주어졌을 때
        long userId = 1L;
        User user = makeEntity(userId, () -> User.builder().build());

        long feedId = 2L;
        Feed feed = makeEntity(feedId, () -> Feed.builder().build());

        long upperCommentId = 3L;
        Comment upperComment = makeEntity(upperCommentId, () -> Comment.builder().build());

        long newReplyCommentId = 4L;
        Comment newReplyComment = makeEntity(newReplyCommentId, () -> Comment.builder().build());
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        CommentCreateDto dto = mock(CommentCreateDto.class);
        when(dto.getUpperReplyId()).thenReturn(upperCommentId);
        when(dto.toEntityWith(user, feed)).thenReturn(newReplyComment);

        //when 댓글 생성 서비스를 실행하면
        Long result = commentService.createComment(dto, feedId, userId);

        //then 주어진 답댓글 ID 가 반환 / 상위 댓글 지정 / 피드 카운트 오름 / 이벤트 발행 / 주어진 새 답댓글이 저장
        assertThat(result).isEqualTo(newReplyCommentId);
        verify(newReplyComment).isBelongTo(upperComment);
        verify(feed).commentCountUp();
        verify(publisher).publishEvent(any(Object.class));
        verify(commentRepository).save(newReplyComment);
    }

    @Test
    @DisplayName("단일 댓글 엔티티 조회 테스트")
    void 단일_댓글_엔티티_조회_테스트() {
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
        //given
        long userId = 1L;
        User user = makeEntity(userId,
                () -> User.builder().build());

        long feedId = 2L;
        Feed feed = makeEntity(feedId,
                () -> Feed.builder().build());

        long noReplyId = 3L;
        Comment noReplyComment = makeEntity(noReplyId,
                () -> Comment.builder().owner(user).feed(feed).build());

        long hasReplyId = 4L;
        Comment hasReplyComment = makeEntity(hasReplyId,
                () -> Comment.builder().owner(user).feed(feed).build());

        long replyId = 5L;
        Comment replyComment = makeEntity(replyId,
                () -> Comment.builder().owner(user).feed(feed).build());
        replyComment.isBelongTo(hasReplyComment);

        when(commentRepository.findFeedComments(feedId))
                .thenReturn(List.of(noReplyComment, hasReplyComment));
        when(commentLikeRepository.findCommentIdsByFeedIdAndUserId(userId, feedId))
                .thenReturn(Set.of(noReplyId, replyId));

        //when
        Collection<CommentInfoDto> result = commentService.findAllByFeed(userId, feedId).getContent();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .anyMatch(dto -> dto.getId().equals(noReplyComment.getId()) && dto.getIsLike())
                .anyMatch(dto -> dto.getId().equals(hasReplyComment.getId()) && !dto.getIsLike() && null !=
                        assertThat(dto.getCommentReply())
                                .anyMatch(replyDto -> replyDto.getId().equals(replyComment.getId()) &&
                                        replyDto.getIsLike()));
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