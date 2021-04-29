package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.feed.Feed;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PictureTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        Feed feed = Feed.builder()
                .description("description")
                .build();
        String url = "http://url.com";

        //when
        Picture picture = Picture.builder()
                .feed(feed)
                .url(url)
                .build();

        //then
        assertThat(picture.getFeed()).isEqualTo(feed);
        assertThat(picture.getUrl()).isEqualTo(url);
    }

}