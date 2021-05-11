package container.restaurant.server.web;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FeedControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    private ImageRepository imageRepository;

    @AfterEach
    public void afterEach() {
        imageRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("내가 작성한 피드 상세")
    public void testGetMyFeedDetail() throws Exception {
        //given
        Feed feed = myFeed;
        List<Image> detailImages = saveImages(feed);

        //expect
        mvc.perform(
                get("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(feed.getId()))
                .andExpect(jsonPath("ownerId").value(feed.getId()))
                .andExpect(jsonPath("restaurantId").value(feed.getRestaurant().getId()))
                .andExpect(jsonPath("ownerNickname").value(feed.getOwner().getNickname()))
                .andExpect(jsonPath("restaurantName").value(feed.getRestaurant().getName()))
                .andExpect(jsonPath("category").value(feed.getCategory().toString()))
                .andExpect(jsonPath("imageUrls").value(
                        detailImages.stream().map(Image::getUrl).collect(Collectors.toList())
                ))
                .andExpect(jsonPath("content").value(feed.getContent()))
                .andExpect(jsonPath("welcome").value(feed.getWelcome()))
                .andExpect(jsonPath("difficulty").value(feed.getDifficulty()))
                .andExpect(jsonPath("likeCount").value(feed.getLikeCount()))
                .andExpect(jsonPath("scrapCount").value(feed.getScrapedCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists());
    }

    @Test
    @DisplayName("남이 작성한 피드 상세")
    public void testGetOthersFeedDetail() throws Exception {
        Feed feed = othersFeed;
        List<Image> detailImages = saveImages(feed);

        //expect
        mvc.perform(get("/api/feed/{feedId}", feed.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(feed.getId()))
                .andExpect(jsonPath("ownerId").value(feed.getId()))
                .andExpect(jsonPath("restaurantId").value(feed.getRestaurant().getId()))
                .andExpect(jsonPath("ownerNickname").value(feed.getOwner().getNickname()))
                .andExpect(jsonPath("restaurantName").value(feed.getRestaurant().getName()))
                .andExpect(jsonPath("category").value(feed.getCategory().toString()))
                .andExpect(jsonPath("imageUrls").value(
                        detailImages.stream().map(Image::getUrl).collect(Collectors.toList())
                ))
                .andExpect(jsonPath("content").value(feed.getContent()))
                .andExpect(jsonPath("welcome").value(feed.getWelcome()))
                .andExpect(jsonPath("difficulty").value(feed.getDifficulty()))
                .andExpect(jsonPath("likeCount").value(feed.getLikeCount()))
                .andExpect(jsonPath("scrapCount").value(feed.getScrapedCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.patch.href").doesNotExist())
                .andExpect(jsonPath("_links.delete.href").doesNotExist());
    }

    @Test
    public void testSelectFeed() throws Exception {

    }

    @Test
    public void testSelectRecommendFeed() throws Exception {

    }

    @Test
    public void testSelectUserFeed() throws Exception {

    }

    @Test
    public void testSelectUserScrapFeed() throws Exception {

    }

    @Test
    public void testSelectRestaurantFeed() throws Exception {

    }

    private List<Image> saveImages(Feed feed) {
        List<Image> detailImages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            detailImages.add(Image.builder()
                    .feed(feed)
                    .url("https://detail" + i)
                    .build());
        }
        detailImages = imageRepository.saveAll(detailImages);
        return detailImages;
    }
}