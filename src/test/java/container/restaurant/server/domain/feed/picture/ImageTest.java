package container.restaurant.server.domain.feed.picture;

import container.restaurant.server.domain.feed.Feed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    @Test
    @DisplayName("빌더 테스트")
    void testBuilder() {
        //given
        Feed feed = Feed.builder()
                .content("description")
                .build();
        String url = "https://url.com";

        //when
        Image picture = Image.builder()
                .url(url)
                .build();

        //then
        assertThat(picture.getUrl()).isEqualTo(url);
    }

}