package container.restaurant.server.domain.comment.like;

import container.restaurant.server.domain.comment.Comment;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.AuthProvider;
import container.restaurant.server.domain.user.User;
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

    @Test
    void test() {
        //given
        User user = em.persist(User.builder().authId("AUTH_ID").authProvider(AuthProvider.KAKAO)
                .nickname("TEST NICKNAME").email("test@test.com").build());

        Restaurant restaurant = em.persist(Restaurant.builder().lon(0.0).lat(0.0)
                .addr("ADDRESS").name("RESTAURANT").build());

        Feed feed1 = em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .category(Category.KOREAN).difficulty(1).welcome(false).build());

        Comment comment1_1 = em.persist(Comment.builder().content("cmt1-1").feed(feed1).owner(user).build());
        Comment comment1_2 = em.persist(Comment.builder().content("cmt1-2").feed(feed1).owner(user).build());
        Comment comment1_3 = em.persist(Comment.builder().content("cmt1-3").feed(feed1).owner(user).build());
        em.persist(CommentLike.of(user, comment1_1));
        em.persist(CommentLike.of(user, comment1_3));

        Feed feed2 = em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .category(Category.KOREAN).difficulty(1).welcome(false).build());

        Comment comment2_1 = em.persist(Comment.builder().content("cmt2-1").feed(feed2).owner(user).build());
        Comment comment2_2 = em.persist(Comment.builder().content("cmt2-2").feed(feed2).owner(user).build());
        Comment comment2_3 = em.persist(Comment.builder().content("cmt2-3").feed(feed2).owner(user).build());
        em.persist(CommentLike.of(user, comment2_2));
        em.persist(CommentLike.of(user, comment2_3));

        //when
        Set<Long> res = commentLikeRepository.test(user.getId(),
                List.of(comment1_1, comment1_2, comment2_1, comment2_2));

        //then
        assertThat(res.size()).isEqualTo(2);
        assertThat(res)
                .anyMatch(id -> id.equals(comment1_1.getId()))
                .anyMatch(id -> id.equals(comment2_2.getId()))
                .allMatch(id -> !id.equals(comment1_2.getId()))
                .allMatch(id -> !id.equals(comment2_1.getId()))
                .allMatch(id -> !id.equals(comment1_3.getId()))
                .allMatch(id -> !id.equals(comment2_3.getId()));
    }

}