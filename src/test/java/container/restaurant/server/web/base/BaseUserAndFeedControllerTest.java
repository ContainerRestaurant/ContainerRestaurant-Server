package container.restaurant.server.web.base;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseUserAndFeedControllerTest extends BaseUserControllerTest {

    @Autowired
    protected FeedRepository feedRepository;

    protected Feed myFeed;
    protected Feed othersFeed;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        myFeed = feedRepository.save(Feed.builder()
                .owner(myself)
                .difficulty(4)
                .welcome(true)
                .build());
        othersFeed = feedRepository.save(Feed.builder()
                .owner(other)
                .difficulty(3)
                .welcome(false)
                .build());
    }

    @Override
    @AfterEach
    public void afterEach() {
        feedRepository.deleteAll();
        super.afterEach();
    }
}
