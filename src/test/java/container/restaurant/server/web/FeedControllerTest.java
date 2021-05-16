package container.restaurant.server.web;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class FeedControllerTest extends BaseUserAndFeedControllerTest {

    private static final String LIST_PATH = "_embedded.feedPreviewDtoList";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ScrapFeedRepository scrapFeedRepository;

    @AfterEach
    public void afterEach() {
        scrapFeedRepository.deleteAll();
        imageRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("내가 작성한 피드 상세")
    public void testGetMyFeedDetail() throws Exception {
        //given
        Feed feed = myFeed;

        //expect
        mvc.perform(
                get("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(feed.getId()))
                .andExpect(jsonPath("ownerId").value(feed.getOwner().getId()))
                .andExpect(jsonPath("restaurantId").value(feed.getRestaurant().getId()))
                .andExpect(jsonPath("ownerNickname").value(feed.getOwner().getNickname()))
                .andExpect(jsonPath("restaurantName").value(feed.getRestaurant().getName()))
                .andExpect(jsonPath("category").value(feed.getCategory().toString()))
                .andExpect(jsonPath("thumbnailUrl").value(feed.getThumbnailUrl()))
                .andExpect(jsonPath("content").value(feed.getContent()))
                .andExpect(jsonPath("welcome").value(feed.getWelcome()))
                .andExpect(jsonPath("difficulty").value(feed.getDifficulty()))
                .andExpect(jsonPath("likeCount").value(feed.getLikeCount()))
                .andExpect(jsonPath("scrapCount").value(feed.getScrapedCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.comments.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists());
    }

    @Test
    @DisplayName("남이 작성한 피드 상세")
    public void testGetOthersFeedDetail() throws Exception {
        //given
        Feed feed = othersFeed;

        //expect
        mvc.perform(get("/api/feed/{feedId}", feed.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(feed.getId()))
                .andExpect(jsonPath("ownerId").value(feed.getOwner().getId()))
                .andExpect(jsonPath("restaurantId").value(feed.getRestaurant().getId()))
                .andExpect(jsonPath("ownerNickname").value(feed.getOwner().getNickname()))
                .andExpect(jsonPath("restaurantName").value(feed.getRestaurant().getName()))
                .andExpect(jsonPath("category").value(feed.getCategory().toString()))
                .andExpect(jsonPath("thumbnailUrl").value(feed.getThumbnailUrl()))
                .andExpect(jsonPath("content").value(feed.getContent()))
                .andExpect(jsonPath("welcome").value(feed.getWelcome()))
                .andExpect(jsonPath("difficulty").value(feed.getDifficulty()))
                .andExpect(jsonPath("likeCount").value(feed.getLikeCount()))
                .andExpect(jsonPath("scrapCount").value(feed.getScrapedCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.comments.href").exists())
                .andExpect(jsonPath("_links.patch.href").doesNotExist())
                .andExpect(jsonPath("_links.delete.href").doesNotExist());
    }

    @Test
    @DisplayName("일반 피드 가져오기")
    public void testSelectFeedPage() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = list.get(list.size() - 1);

        //expect
        mvc.perform(
                get("/api/feed")
                        .queryParam("page", "0")
                        .queryParam("size", "2"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(2)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(lastFeed.getId()))
                .andExpect(jsonPath(LIST_PATH + "[0].ownerNickname").value(lastFeed.getOwner().getNickname()))
                .andExpect(jsonPath(LIST_PATH + "[0].content").value(lastFeed.getContent()))
                .andExpect(jsonPath(LIST_PATH + "[0].likeCount").value(lastFeed.getLikeCount()))
                .andExpect(jsonPath(LIST_PATH + "[0].replyCount").value(lastFeed.getReplyCount()))
                .andExpect(jsonPath(LIST_PATH + "[0]._links.self.href").exists())
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("사용자 피드 가져오기")
    public void testSelectUserFeed() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = list.get(list.size() - 1);

        //expect
        mvc.perform(
                get("/api/feed/user/{userId}", other.getId())
                        .queryParam("page", "0")
                        .queryParam("size", "3"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(3)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(lastFeed.getId()))
                .andExpect(jsonPath(LIST_PATH + "[0].ownerNickname").value(lastFeed.getOwner().getNickname()))
                .andExpect(jsonPath(LIST_PATH + "[0].content").value(lastFeed.getContent()))
                .andExpect(jsonPath(LIST_PATH + "[0].likeCount").value(lastFeed.getLikeCount()))
                .andExpect(jsonPath(LIST_PATH + "[0].replyCount").value(lastFeed.getReplyCount()))
                .andExpect(jsonPath(LIST_PATH + "[0]._links.self.href").exists())
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("식당 피드 가져오기")
    public void testSelectRestaurantFeed() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = list.get(list.size() - 1);

        //expect
        mvc.perform(
                get("/api/feed/restaurant/{restaurantId}", restaurant.getId())
                        .queryParam("page", "0")
                        .queryParam("size", "3"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(3)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(lastFeed.getId()))
                .andExpect(jsonPath(LIST_PATH + "[0].ownerNickname").value(lastFeed.getOwner().getNickname()))
                .andExpect(jsonPath(LIST_PATH + "[0].content").value(lastFeed.getContent()))
                .andExpect(jsonPath(LIST_PATH + "[0].likeCount").value(lastFeed.getLikeCount()))
                .andExpect(jsonPath(LIST_PATH + "[0].replyCount").value(lastFeed.getReplyCount()))
                .andExpect(jsonPath(LIST_PATH + "[0]._links.self.href").exists())
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("사용자가 스크랩한 피드 가져오기")
    public void testSelectUserScrapFeed() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = null;
        for (int i = 0; i < 10; i+=3) {
            scrapFeedRepository.save(ScrapFeed.of(myself, list.get(i)));
            lastFeed = list.get(i);
            // 각 스크랩간에 생성 시간차를 두기위해 잠시 대기
            Thread.sleep(0, 1);
        }

        //expect
        mvc.perform(
                get("/api/feed/user/{userId}/scrap", myself.getId())
                        .queryParam("page", "0")
                        .queryParam("size", "2"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(2)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(lastFeed.getId()))
                .andExpect(jsonPath(LIST_PATH + "[0].ownerNickname").value(lastFeed.getOwner().getNickname()))
                .andExpect(jsonPath(LIST_PATH + "[0].content").value(lastFeed.getContent()))
                .andExpect(jsonPath(LIST_PATH + "[0].likeCount").value(lastFeed.getLikeCount()))
                .andExpect(jsonPath(LIST_PATH + "[0].replyCount").value(lastFeed.getReplyCount()))
                .andExpect(jsonPath(LIST_PATH + "[0]._links.self.href").exists())
                .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    public void testSelectRecommendFeed() throws Exception {

    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("피드 쓰기")
    public void testCreateFeed() throws Exception {
        //given
        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantId(restaurant.getId())
                .category(Category.KOREAN)
                .difficulty(3)
                .welcome(true)
                .thumbnailUrl("https://test.feed.thumbnail")
                .content("this is feed's content")
                .build();

        //when
        mvc.perform(
                post("/api/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .session(myselfSession))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location", Matchers.containsString("/api/feed/")));
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("피드 쓰기 실패")
    public void testCreateFeedFailed() throws Exception {
        //given
        FeedInfoDto dto = FeedInfoDto.builder()
                .welcome(true)
                .thumbnailUrl("https://test.feed.thumbnail")
                .content("this is feed's content")
                .build();

        //when
        mvc.perform(
                post("/api/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .session(myselfSession))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("피드 삭제")
    public void testDeleteFeed() throws Exception {
        //given
        Feed feed = myFeed;

        //when
        mvc.perform(
                delete("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andExpect(status().isNoContent());

        //expect
        assertThat(feedRepository.existsById(feed.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("피드 삭제 실패 - 존재않는 피드")
    public void testFailedDeleteFeedById() throws Exception {
        //given
        Long invalidId = -1L;

        //when
        mvc.perform(
                delete("/api/feed/{feedId}", invalidId)
                        .session(myselfSession))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("피드 삭제 실패 - 다른 사용자의 피드")
    public void testFailedDeleteFeedBySession() throws Exception {
        //given
        Feed feed = othersFeed;

        //when
        mvc.perform(
                delete("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andExpect(status().isForbidden());

        //expect
        assertThat(feedRepository.existsById(feed.getId())).isTrue();
    }

    private List<Feed> saveFeeds() throws InterruptedException {
        Restaurant tempRestaurant = restaurantRepository.save(Restaurant.builder()
                .name("restaurant")
                .addr("address")
                .lon(0f)
                .lat(0f)
                .image_ID(image.getId())
                .build());

        List<Feed> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(feedRepository.save(Feed.builder()
                    .owner(i % 2 == 0 ? myself : other)
                    .restaurant(i % 2 == 0? tempRestaurant : restaurant)
                    .difficulty(4)
                    .category(Category.JAPANESE)
                    .welcome(true)
                    .thumbnailUrl("https://my.thumbnail" + 1)
                    .content("Feed Content")
                    .build()));
            // 각 피드간에 생성시간차를 두기위해 잠시 대기
            Thread.sleep(0, 1);
        }
        return list;
    }
}