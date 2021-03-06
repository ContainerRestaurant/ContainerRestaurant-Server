package container.restaurant.server.web;

import com.fasterxml.jackson.core.type.TypeReference;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLike;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedMenuDto;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class FeedControllerTest extends BaseUserAndFeedControllerTest {

    private static final String LIST_PATH = "_embedded.feedPreviewDtoList";

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
        super.afterEach();
    }

    @Test
    @DisplayName("?????? ????????? ?????? ??????")
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
                .andExpect(jsonPath("thumbnailUrl").value(containsString(feed.getThumbnail().getUrl())))
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
    @DisplayName("?????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ????????? ?????? ??????")
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
                .andExpect(jsonPath("thumbnailUrl").value(containsString(feed.getThumbnail().getUrl())))
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
                .andExpect(jsonPath("_links.delete.href").doesNotExist())
                .andDo(document("feed-detail",
                        links(
                                linkWithRel("self").description("??? ????????? ??????"),
                                linkWithRel("owner").description("??? ????????? ????????? ????????? ??????"),
                                linkWithRel("restaurant").description("??? ????????? ????????? ?????? ??????"),
                                linkWithRel("comments").description("??? ????????? ?????? ?????? ????????? ??????"),
                                linkWithRel("like").description("??? ????????? ??????????????? ??????"),
                                linkWithRel("scrap").description("??? ????????? ??????????????? ??????"),
                                linkWithRel("patch").optional()
                                        .description("??? ????????? ???????????? ?????? (??? ?????? ???????????? ??????)"),
                                linkWithRel("delete").optional()
                                        .description("??? ????????? ???????????? ?????? (??? ?????? ???????????? ??????)"),
                                linkWithRel("report").optional()
                                        .description("??? ????????? ???????????? ?????? (??? ?????? ???????????? ?????? ??????)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("??? ????????? ?????????"),
                                fieldWithPath("ownerId").description("??? ????????? ????????? ???????????? ?????????"),
                                fieldWithPath("ownerContainerLevel").description("??? ????????? ????????? ???????????? ?????? ??????"),
                                fieldWithPath("ownerProfile").description("??? ????????? ????????? ???????????? ????????? ????????????"),
                                fieldWithPath("restaurantId").description("??? ????????? ????????? ????????? ?????????"),
                                fieldWithPath("ownerNickname").description("??? ????????? ????????? ???????????? ?????????"),
                                fieldWithPath("restaurantName").description("??? ????????? ????????? ????????? ??????"),
                                fieldWithPath("category").description("??? ????????? ????????? ????????? ????????????"),
                                fieldWithPath("thumbnailUrl").description("??? ????????? ????????? ??? ????????? ??????"),
                                fieldWithPath("content").description("??? ????????? ?????????"),
                                fieldWithPath("welcome").description("??? ????????? ????????? ???????????? ???????????? ??????????????? ??????"),
                                fieldWithPath("difficulty").description("??? ????????? ????????? ????????? ?????????"),
                                fieldWithPath("likeCount").description("??? ????????? ????????? ??????"),
                                fieldWithPath("scrapCount").description("??? ????????? ????????? ??????"),
                                fieldWithPath("replyCount").description("??? ????????? ?????? ??????"),
                                fieldWithPath("isLike").description("?????? ???????????? ??? ????????? ????????? ????????? ??????"),
                                fieldWithPath("isScraped").description("?????? ???????????? ??? ????????? ????????? ????????? ??????"),
                                fieldWithPath("isContainerFriendly").description("?????? ?????? ?????? ??????"),
                                subsectionWithPath("mainMenu").description("??? ????????? ????????? ?????? ?????? ?????????"),
                                subsectionWithPath("subMenu").description("??? ????????? ????????? ?????? ?????? ?????????"),
                                subsectionWithPath("_links").description("??? ???????????? ?????? ????????? ?????? ??????")
                        )))
                .andDo(document("feed-menu",
                        responseFields(beneathPath("mainMenu[]"),
                                fieldWithPath("menuName").description("?????? ?????? ??????"),
                                fieldWithPath("container").description("????????? ????????? ?????? ??????")
                        )));
    }

    @Test
    @DisplayName("?????? ?????? ????????????")
    public void testSelectFeedPage() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed testFeed = list.get(list.size() - 3);

        //expect
        mvc.perform(
                get("/api/feed")
                        .queryParam("page", "1")
                        .queryParam("size", "2"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(2)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(testFeed.getId()))
                .andExpect(jsonPath(LIST_PATH + "[0].ownerNickname").value(testFeed.getOwner().getNickname()))
                .andExpect(jsonPath(LIST_PATH + "[0].content").value(testFeed.getContent()))
                .andExpect(jsonPath(LIST_PATH + "[0].likeCount").value(testFeed.getLikeCount()))
                .andExpect(jsonPath(LIST_PATH + "[0].replyCount").value(testFeed.getReplyCount()))
                .andExpect(jsonPath(LIST_PATH + "[0]._links.self.href").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-preview",
                        responseFields(beneathPath(LIST_PATH),
                                fieldWithPath("id").description("?????? ?????? ?????????"),
                                fieldWithPath("thumbnailUrl").description("?????? ?????? ????????? ????????? ??????"),
                                fieldWithPath("ownerNickname").description("?????? ?????? ????????? ?????????"),
                                fieldWithPath("content").description("?????? ?????? ?????????"),
                                fieldWithPath("likeCount").description("?????? ?????? ????????? ??????"),
                                fieldWithPath("replyCount").description("?????? ?????? ?????? ??????"),
                                fieldWithPath("_links.self.href").description("?????? ????????? ?????? ?????? ??????"),
                                fieldWithPath("isContainerFriendly").description("?????? ?????? ?????? ??????"),
                                fieldWithPath("isLike").description("???????????? ???????????? ?????? ????????? ??????"),
                                fieldWithPath("isScraped").description("???????????? ???????????? ?????? ????????? ??????")
                        )))
                .andDo(document("feed-list",
                        links(
                                linkWithRel("self").description("??? ????????? ??????"),
                                linkWithRel("create").description("??? ????????? ???????????? ??????"),
                                linkWithRel("category-list").description("????????? ???????????? ??????"),
                                linkWithRel("next").description("?????? ?????? ?????????"),
                                linkWithRel("last").description("????????? ?????? ?????????"),
                                linkWithRel("prev").description("?????? ?????? ?????????"),
                                linkWithRel("first").description("??? ?????? ?????????")
                        ),
                        responseFields(
                                subsectionWithPath(LIST_PATH).description("?????? ???????????? ????????? (<<feed-preview,?????? ??????>>)"),
                                subsectionWithPath("_links").description("??? ???????????? ?????? ????????? ?????? ??????"),
                                fieldWithPath("page.size").description("??? ?????? ???????????? ????????? ??? ?????? ??????"),
                                fieldWithPath("page.totalElements").description("??? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("page.totalPages").description("??? ?????? ???????????? ?????? ????????? ???"),
                                fieldWithPath("page.number").description("??? ?????? ??????????????? ?????? ?????????")

                        )));
    }

    @Test
    @DisplayName("????????? ?????? ????????????")
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-user"));
    }

    @Test
    @DisplayName("?????? ?????? ????????????")
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-restaurant"));
    }

    @Test
    @DisplayName("???????????? ???????????? ?????? ????????????")
    public void testSelectUserScrapFeed() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = null;
        for (int i = 0; i < 10; i += 3) {
            scrapFeedRepository.save(ScrapFeed.of(myself, list.get(i)));
            lastFeed = list.get(i);
            // ??? ??????????????? ?????? ???????????? ???????????? ?????? ??????
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-scrap"));
    }

    @Test
    @DisplayName("?????? ?????? ????????????")
    public void testSelectRecommend() throws Exception {
        List<Feed> list = saveFeeds();
        for (int i = 0; i < 10; i += 3) {
            scrapFeedRepository.save(ScrapFeed.of(myself, list.get(i)));
        }

        mvc.perform(get("/api/feed/recommend"))
                .andExpect(status().isOk())
                .andDo(document("feed-recommendation"));

    }

    @Test
    @DisplayName("???????????? ???????????? ?????? ?????????")
    public void testCategoryFilter() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed testFeed = list.get(list.size() - 1);

        mvc.perform(

                get("/api/feed")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("category", "japanese")
                        .queryParam("sort", "createdDate,DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(LIST_PATH, hasSize(2)))
                .andExpect(jsonPath(LIST_PATH + "[0].id").value(testFeed.getId()))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.create.href").exists())
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-options",
                        requestParameters(
                                parameterWithName("category").description(
                                        "???????????? ???????????? (???????????? ?????? X)"),
                                parameterWithName("sort").description(
                                        "?????? ?????? [createdDate|likeCount|difficulty],[ASC,DESC] +\n" +
                                                "?????????: createdDate,DESC"),
                                parameterWithName("size").description(
                                        "???????????? ?????? ?????? +\n?????????: 20"),
                                parameterWithName("page").description(
                                        "????????? ????????? +\n?????????: 0")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ??????")
    public void testCreateFeed() throws Exception {
        //given
        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantCreateDto(RestaurantInfoDto.from(restaurant))
                .category(Category.KOREAN)
                .difficulty(3)
                .welcome(true)
                .thumbnailImageId(image.getId())
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
                .andExpect(header().string("Location", Matchers.containsString("/api/feed/")))
                .andDo(document("restaurant-register",
                        requestFields(beneathPath("restaurantCreateDto"),
                                fieldWithPath("name").description("????????? ?????? ??????"),
                                fieldWithPath("address").description("????????? ????????? ??????"),
                                fieldWithPath("latitude").description("????????? ?????? ????????? ?????????"),
                                fieldWithPath("longitude").description("????????? ?????? ????????? ?????????")
                        )))
                .andDo(document("feed-create",
                        requestFields(
                                subsectionWithPath("restaurantCreateDto")
                                        .description("????????? ????????? ????????? (<<restaurant-register,?????? ??????>>)"),
                                fieldWithPath("category").description("????????? ????????????"),
                                subsectionWithPath("mainMenu").description("?????? ?????? ?????????"),
                                subsectionWithPath("subMenu").description("(Optional) ?????? ?????????"),
                                fieldWithPath("difficulty").description("?????? ?????????"),
                                fieldWithPath("welcome").description("(Optional) ????????? ?????? ??????"),
                                fieldWithPath("thumbnailImageId").description("(Optional) ????????? ????????? ?????????"),
                                fieldWithPath("content").description("(Optional) ?????? ?????????")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ?????? ??????")
    public void testCreateFeedFailed() throws Exception {
        //given
        FeedInfoDto dto = FeedInfoDto.builder()
                .welcome(true)
                .thumbnailImageId(1L)
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
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ??????")
    public void testUpdateFeed() throws Exception {
        //given
        Feed feed = myFeed;
        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantCreateDto(RestaurantInfoDto.from(restaurant))
                .category(Category.KOREAN)
                .difficulty(myFeed.getDifficulty() + 1)
                .welcome(!myFeed.getWelcome())
                .thumbnailImageId(image.getId())
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
                .andExpect(status().isOk())
                .andDo(document("feed-update",
                        requestFields(
                                subsectionWithPath("restaurantCreateDto")
                                        .description("(Optional) ????????? ????????? ????????? (<<restaurant-register,?????? ??????>>)"),
                                fieldWithPath("category").description("(Optional) ????????? ????????????"),
                                subsectionWithPath("mainMenu").description("(Optional) ?????? ?????? ?????????"),
                                subsectionWithPath("subMenu").description("(Optional) ?????? ?????????"),
                                fieldWithPath("difficulty").description("(Optional) ?????? ?????????"),
                                fieldWithPath("welcome").description("(Optional) ????????? ?????? ??????"),
                                fieldWithPath("thumbnailImageId").description("(Optional) ????????? ????????? ?????????"),
                                fieldWithPath("content").description("(Optional) ?????? ?????????")
                        )));

        //then
        feed = feedRepository.findById(feed.getId())
                .orElseThrow(() -> new RuntimeException(""));

        assertThat(feed.getCategory()).isEqualTo(dto.getCategory());
        assertThat(feed.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(feed.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(feed.getThumbnail().getId()).isEqualTo(dto.getThumbnailImageId());
        assertThat(feed.getContent()).isEqualTo(dto.getContent());
        assertThat(feed.getContainerList()).hasSize(2);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ?????? ??????")
    public void testFailedUpdateFeed() throws Exception {
        //given
        Feed feed = myFeed;
        Image newImage = imageRepository.save(Image.builder()
                .url("https://new.thumbnail")
                .build());
        FeedInfoDto dto = FeedInfoDto.builder()
                .category(Category.KOREAN)
                .difficulty(myFeed.getDifficulty() + 1)
                .welcome(!myFeed.getWelcome())
                .thumbnailImageId(newImage.getId())
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
        assertThat(feed.getThumbnail().getId()).isNotEqualTo(dto.getThumbnailImageId());
        assertThat(feed.getContent()).isNotEqualTo(dto.getContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ??????")
    public void testDeleteFeed() throws Exception {
        //given
        Feed feed = myFeed;

        //when
        mvc.perform(
                delete("/api/feed/{feedId}", feed.getId())
                        .session(myselfSession))
                .andExpect(status().isNoContent())
                .andDo(document("feed-delete"));

        //expect
        assertThat(feedRepository.existsById(feed.getId())).isFalse();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ?????? ?????? - ???????????? ??????")
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
    @WithMockUser(roles = "USER")
    @DisplayName("?????? ?????? ?????? - ?????? ???????????? ??????")
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
    @DisplayName("???????????? ????????? ?????????")
    public void testGetCategoryList() throws Exception {
        //given
        MvcResult res = mvc.perform(get("/api/feed/category"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("feed-category-list"))
                .andReturn();

        Map<String, String> map =
                mapper.readValue(
                        res.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<HashMap<String, String>>() {
                        });

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
                .thumbnail(image)
                .build());

        List<Feed> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(feedRepository.save(Feed.builder()
                    .owner(i % 2 == 0 ? myself : other)
                    .restaurant(i % 2 == 0 ? tempRestaurant : restaurant)
                    .difficulty(4)
                    .category(Category.JAPANESE)
                    .welcome(true)
                    .thumbnail(image)
                    .content("Feed Content")
                    .build()));
            // ??? ???????????? ?????????????????? ???????????? ?????? ??????
            Thread.sleep(0, 1);
        }
        return list;
    }
}