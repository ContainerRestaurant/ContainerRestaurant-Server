package container.restaurant.server.domain.comment;

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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest2 {

    @Autowired TestEntityManager em;
    @Autowired CommentRepository commentRepository;

    User owner;
    Restaurant restaurant;

    @BeforeEach
    void 유저_레스토랑_세팅() {
        owner = em.persist(User.builder().authId("authID").authProvider(AuthProvider.KAKAO).build());
        restaurant = em.persist(Restaurant.builder()
                .name("restaurant").addr("address")
                .lat(36.5).lon(36.5).build());
    }

    @Test
    @DisplayName("피드의 댓글 조회")
    void 피드의_댓글_조회() {
        //given 피드1,2 가 주어지고, 피드1의 댓글, 답글 있는 댓글, 답글과 피드 2의 댓글이 주어지고, em 을 비웠을 때
        Feed feed1 = em.persist(Feed.builder()
                .owner(owner).restaurant(restaurant).difficulty(3)
                .category(Category.KOREAN).build());

        Feed feed2 = em.persist(Feed.builder()
                .owner(owner).restaurant(restaurant).difficulty(3)
                .category(Category.KOREAN).build());

        Comment normalComment = em.persist(Comment.builder().owner(owner).feed(feed1).build());

        Comment hasReplyComment = em.persist(Comment.builder().owner(owner).feed(feed1).build());

        Comment replyComment1 = em.persist(Comment.builder().owner(owner).feed(feed1).build());
        replyComment1.isBelongTo(hasReplyComment);

        Comment replyComment2 = em.persist(Comment.builder().owner(owner).feed(feed1).build());
        replyComment2.isBelongTo(hasReplyComment);

        em.persist(Comment.builder().owner(owner).feed(feed2).build());

        em.flush();
        em.clear();

        //when 피드1의 ID 로 댓글을 조회하면
        List<Comment> result = commentRepository.findFeedComments(feed1.getId());

        //then 댓글들이 조회되고, 답글이 있는 댓글은 답글이 포함되어있다.
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .anyMatch(c -> c.getId().equals(normalComment.getId()))
                .anyMatch(c -> c.getId().equals(hasReplyComment.getId()) && null !=
                        assertThat(c.getReplies())
                                .anyMatch(r -> r.getId().equals(replyComment1.getId()))
                                .anyMatch(r -> r.getId().equals(replyComment2.getId())));
    }

}
