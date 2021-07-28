package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.AuthProvider;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentLikeRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    User user;
    Restaurant restaurant;

    @BeforeEach
    void 유저_레스토랑_세팅() {
        user = em.persist(User.builder().authId("AUTH_ID").authProvider(AuthProvider.KAKAO).build());
        restaurant = em.persist(Restaurant.builder()
                .lon(0.0).lat(0.0)
                .addr("ADDRESS").name("RESTAURANT").build());
    }

    @Test
    @DisplayName("유저, 피드 정보로 좋아요한 댓글 ID 조회하기")
    public void 유저_피드_정보로_좋아요한_댓글_ID_조회하기() {
        //given
        User other = em.persist(User.builder().authId("AUTH_ID").authProvider(AuthProvider.KAKAO).build());

        Feed feed1 = em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .category(Category.KOREAN).difficulty(1).welcome(false).build());
        Comment likedComment1 = em.persist(Comment.builder().owner(user).feed(feed1).build());
        Comment likedComment2 = em.persist(Comment.builder().owner(user).feed(feed1).build());
        Comment nonLikeComment = em.persist(Comment.builder().owner(user).feed(feed1).build());

        Feed feed2 = em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .category(Category.KOREAN).difficulty(1).welcome(false).build());
        Comment otherFeedComment = em.persist(Comment.builder().owner(user).feed(feed2).build());

        em.persist(CommentLike.of(user, likedComment1));
        em.persist(CommentLike.of(user, likedComment2));
        em.persist(CommentLike.of(user, otherFeedComment));

        em.persist(CommentLike.of(other, likedComment1));

        //when
        Set<Long> result = commentLikeRepository.findCommentIdsByFeedIdAndUserId(user.getId(), feed1.getId());

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .anyMatch(likedComment1.getId()::equals)
                .anyMatch(likedComment2.getId()::equals)
                .noneMatch(nonLikeComment.getId()::equals)
                .noneMatch(otherFeedComment.getId()::equals);
    }

    @Test
    @DisplayName("유저, 피드 정보로 좋아요한 댓글 ID 조회하기 - 유저 null")
    public void 유저_피드_정보로_좋아요한_댓글_ID_조회하기__유저_null() {
        //given
        Feed feed1 = em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .category(Category.KOREAN).difficulty(1).welcome(false).build());
        Comment likedComment = em.persist(Comment.builder().owner(user).feed(feed1).build());
        em.persist(Comment.builder().owner(user).feed(feed1).build());

        em.persist(CommentLike.of(user, likedComment));

        //when
        Set<Long> result = commentLikeRepository.findCommentIdsByFeedIdAndUserId(null, feed1.getId());

        //then
        assertThat(result.isEmpty()).isTrue();
    }

}