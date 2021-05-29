package container.restaurant.server.web;

import com.fasterxml.jackson.core.type.TypeReference;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLike;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedMenuDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private FeedLikeRepository feedLikeRepository;

    @Autowired
    private FeedHitRepository feedHitRepository;

    @AfterEach
    public void afterEach() {
        feedHitRepository.deleteAll();
        feedLikeRepository.deleteAll();
        scrapFeedRepository.deleteAll();
        imageRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @WithMockUser
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
                .andExpect(jsonPath("scrapCount").value(feed.getScrapCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("isLike").value(false))
                .andExpect(jsonPath("isScraped").value(false))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.owner.href").exists())
                .andExpect(jsonPath("_links.restaurant.href").exists())
                .andExpect(jsonPath("_links.comments.href").exists())
                .andExpect(jsonPath("_links.patch.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists())
                .andExpect(jsonPath("_links.like.href").exists())
                .andExpect(jsonPath("_links.scrap.href").exists());

        assertThat(feedRepository.findById(feed.getId()).orElse(feed).getHitCount())
                .isEqualTo(1);
    }

    @Test
    @WithMockUser
    @DisplayName("내가 좋아요한 피드 상세")
    public void testGetMyLikeFeedDetail() throws Exception {
        //given
        Feed feed = myFeed;
        feedLikeRepository.save(FeedLike.of(myself, myFeed));

        //expect
        mvc.perform(
                get("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("isLike").value(true))
                .andExpect(jsonPath("isScraped").value(false))
                .andExpect(jsonPath("_links.like-cancel.href").exists())
                .andExpect(jsonPath("_links.scrap.href").exists());

        assertThat(feedRepository.findById(feed.getId()).orElse(feed).getHitCount())
                .isEqualTo(1);
    }

    @Test
    @WithMockUser
    @DisplayName("내가 스크랩한 피드 상세")
    public void testGetMyScrapFeedDetail() throws Exception {
        //given
        Feed feed = myFeed;
        scrapFeedRepository.save(ScrapFeed.of(myself, myFeed));

        //expect
        mvc.perform(
                get("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("isLike").value(false))
                .andExpect(jsonPath("isScraped").value(true))
                .andExpect(jsonPath("_links.like.href").exists())
                .andExpect(jsonPath("_links.scrap-cancel.href").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("내가 좋아요와 스크랩한 피드 상세")
    public void testGetMyLikeAndScrapFeedDetail() throws Exception {
        //given
        Feed feed = myFeed;
        feedLikeRepository.save(FeedLike.of(myself, myFeed));
        scrapFeedRepository.save(ScrapFeed.of(myself, myFeed));

        //expect
        mvc.perform(
                get("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("isLike").value(true))
                .andExpect(jsonPath("isScraped").value(true))
                .andExpect(jsonPath("_links.like-cancel.href").exists())
                .andExpect(jsonPath("_links.scrap-cancel.href").exists());
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
                .andExpect(jsonPath("scrapCount").value(feed.getScrapCount()))
                .andExpect(jsonPath("replyCount").value(feed.getReplyCount()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.owner.href").exists())
                .andExpect(jsonPath("_links.restaurant.href").exists())
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
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists());
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
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists());
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
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists());
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
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists());
    }

    @Test
    @DisplayName("카테고리 필터링 테스트")
    public void testCategoryFilter() throws Exception{
        mvc.perform(get("/api/feed?category=korean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(1)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(othersFeed.getId()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists());
    }

    @Test
    @WithMockUser
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
                .mainMenu(List.of(FeedMenuDto.builder()
                        .menuName("rice cake")
                        .container("fry pan")
                        .build()))
                .subMenu(List.of(FeedMenuDto.builder()
                        .menuName("source")
                        .container("mini cup")
                        .build()))
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
    @WithMockUser
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
    @WithMockUser
    @DisplayName("피드 수정")
    public void testUpdateFeed() throws Exception {
        //given
        Feed feed = myFeed;
        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantId(restaurant.getId())
                .category(Category.KOREAN)
                .difficulty(myFeed.getDifficulty() + 1)
                .welcome(!myFeed.getWelcome())
                .thumbnailUrl(myFeed.getThumbnailUrl() + ".update")
                .content("update feed!")
                .mainMenu(List.of(FeedMenuDto.builder()
                        .menuName("rice cake")
                        .container("fry pan")
                        .build()))
                .subMenu(List.of(FeedMenuDto.builder()
                        .menuName("source")
                        .container("mini cup")
                        .build()))
                .build();

        //when
        mvc.perform(
                patch("/api/feed/{feedId}", feed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .session(myselfSession))
                .andExpect(status().isOk());

        //then
        feed = feedRepository.findById(feed.getId())
                .orElseThrow(() -> new RuntimeException(""));

        assertThat(feed.getCategory()).isEqualTo(dto.getCategory());
        assertThat(feed.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(feed.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(feed.getThumbnailUrl()).isEqualTo(dto.getThumbnailUrl());
        assertThat(feed.getContent()).isEqualTo(dto.getContent());
        assertThat(feed.getContainerList()).hasSize(2);
    }

    @Test
    @WithMockUser
    @DisplayName("피드 수정 실패")
    public void testFailedUpdateFeed() throws Exception {
        //given
        Feed feed = myFeed;
        FeedInfoDto dto = FeedInfoDto.builder()
                .category(Category.KOREAN)
                .difficulty(myFeed.getDifficulty() + 1)
                .welcome(!myFeed.getWelcome())
                .thumbnailUrl(myFeed.getThumbnailUrl() + ".update")
                .content("update feed!")
                .build();

        //when
        mvc.perform(
                patch("/api/feed/{feedId}", feed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .session(myselfSession))
                .andExpect(status().isBadRequest());

        //then
        feed = feedRepository.findById(feed.getId())
                .orElseThrow(() -> new RuntimeException(""));

        assertThat(feed.getCategory()).isNotEqualTo(dto.getCategory());
        assertThat(feed.getDifficulty()).isNotEqualTo(dto.getDifficulty());
        assertThat(feed.getWelcome()).isNotEqualTo(dto.getWelcome());
        assertThat(feed.getThumbnailUrl()).isNotEqualTo(dto.getThumbnailUrl());
        assertThat(feed.getContent()).isNotEqualTo(dto.getContent());
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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

    @Test
    @DisplayName("카테고리 리스트 테스트")
    public void testGetCategoryList() throws Exception {
        //given
        MvcResult res = mvc.perform(get("/api/feed/category"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Map<String, String> map =
                mapper.readValue(
                        res.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<HashMap<String, String>>(){});

        //expect
        for (Category category : Category.values()) {
            assertThat(map.remove(category.name()))
                    .isEqualTo(category.getKorean());
        }
        assertThat(map.size()).isEqualTo(0);
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