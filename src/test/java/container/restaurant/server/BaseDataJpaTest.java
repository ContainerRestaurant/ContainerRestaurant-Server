package container.restaurant.server;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.OAuth2Identifier;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public abstract class BaseDataJpaTest {

    @Autowired
    protected TestEntityManager em;

    protected Feed newFeed(User user, Restaurant restaurant) {
        return em.persist(Feed.builder().owner(user).restaurant(restaurant)
                .difficulty(3).category(Category.KOREAN)
                .build());
    }

    protected Restaurant newRestaurant() {
        return em.persist(Restaurant.builder()
                .name("restaurant").addr("address").lon(0f).lat(0f)
                .build());
    }

    protected User newUser() {
        return em.persist(User.builder().email("test@test.com").nickname("testNickname")
                .identifier(OAuth2Identifier.of("authId", OAuth2Registration.KAKAO))
                .build());
    }

}
