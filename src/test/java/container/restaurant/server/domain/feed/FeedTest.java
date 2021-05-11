package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FeedTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        User user = User.builder()
                .email("test@test.com")
                .profile("https://my.profile")
                .build();
        String description = "description";
        Boolean welcome = true;
        Integer difficulty = 5;
        LocalDateTime now = LocalDateTime.now();

        //when
        Feed feed = Feed.builder()
                .owner(user)
                .content(description)
                .welcome(welcome)
                .difficulty(difficulty)
                .build();

        //then
        assertThat(feed.getId()).isNull();
        assertThat(feed.getOwner()).isEqualTo(user);
        assertThat(feed.getContent()).isEqualTo(description);
        assertThat(feed.getWelcome()).isEqualTo(welcome);
        assertThat(feed.getDifficulty()).isEqualTo(difficulty);
        assertThat(feed.getLikeCount()).isEqualTo(0);
        assertThat(feed.getScrapedCount()).isEqualTo(0);
        assertThat(feed.getReplyCount()).isEqualTo(0);
        assertThat(feed.getIsBlind()).isFalse();
        assertThat(feed.getIsDeleted()).isFalse();
        assertThat(feed.getCreatedDate()).isNull();
        assertThat(feed.getModifiedDate()).isNull();
    }

}