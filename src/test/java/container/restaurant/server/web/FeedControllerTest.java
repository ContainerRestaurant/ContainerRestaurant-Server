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
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("owner").description("본 피드를 작성한 사용자 링크"),
                                linkWithRel("restaurant").description("본 피드에 등록된 식당 링크"),
                                linkWithRel("comments").description("본 피드에 달린 댓글 리스트 링크"),
                                linkWithRel("like").description("본 피드를 좋아요하는 링크"),
                                linkWithRel("scrap").description("본 피드를 스크랩하는 링크"),
                                linkWithRel("patch").optional()
                                        .description("본 피드를 수정하는 링크 (본 피드 작성자인 경우)"),
                                linkWithRel("delete").optional()
                                        .description("본 피드를 삭제하는 링크 (본 피드 작성자인 경우)"),
                                linkWithRel("report").optional()
                                        .description("본 피드를 신고하는 링크 (본 피드 작성자가 아닌 경우)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("본 피드의 식별값"),
                                fieldWithPath("ownerId").description("본 피드를 작성한 사용자의 식별값"),
                                fieldWithPath("ownerContainerLevel").description("본 피드를 작성한 사용자의 레벨 정보"),
                                fieldWithPath("ownerProfile").description("본 피드를 작성한 사용자의 프로필 이미지ㅅ"),
                                fieldWithPath("restaurantId").description("본 피드에 등록된 식당의 식별값"),
                                fieldWithPath("ownerNickname").description("본 피드를 작성한 사용자의 닉네임"),
                                fieldWithPath("restaurantName").description("본 피드에 등록된 삭당의 이름"),
                                fieldWithPath("category").description("본 피드에 등록된 식당의 카테고리"),
                                fieldWithPath("thumbnailUrl").description("본 피드에 썸네일 및 업로드 사진"),
                                fieldWithPath("content").description("본 피드의 콘텐트"),
                                fieldWithPath("welcome").description("본 피드에 등록된 식당에서 사장님이 환영했는지 여부"),
                                fieldWithPath("difficulty").description("본 피드에 등록된 음식의 난이도"),
                                fieldWithPath("likeCount").description("본 피드의 좋아요 개수"),
                                fieldWithPath("scrapCount").description("본 피드의 스크랩 횟수"),
                                fieldWithPath("replyCount").description("본 피드의 댓글 개수"),
                                fieldWithPath("isLike").description("현재 사용자가 본 피드를 좋아요 했는지 여부"),
                                fieldWithPath("isScraped").description("현재 사용자가 본 피드를 스크랩 했는지 여부"),
                                fieldWithPath("isContainerFriendly").description("용기 친화 식당 여부"),
                                subsectionWithPath("mainMenu").description("본 피드에 등록된 메인 메뉴 리스트"),
                                subsectionWithPath("subMenu").description("본 피드에 등록된 서브 메뉴 리스트"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 명세")
                        )))
                .andDo(document("feed-menu",
                        responseFields(beneathPath("mainMenu[]"),
                                fieldWithPath("menuName").description("음식 메뉴 이름"),
                                fieldWithPath("container").description("음식에 사용한 용기 설명")
                        )));
    }

    @Test
    @DisplayName("일반 피드 가져오기")
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
                                fieldWithPath("id").description("해당 피드 식별값"),
                                fieldWithPath("thumbnailUrl").description("해당 피드 썸네일 이미지 링크"),
                                fieldWithPath("ownerNickname").description("해당 피드 작성자 닉네임"),
                                fieldWithPath("content").description("해당 피드 콘텐트"),
                                fieldWithPath("likeCount").description("해당 피드 좋아요 개수"),
                                fieldWithPath("replyCount").description("해당 피드 답글 개수"),
                                fieldWithPath("_links.self.href").description("해당 피드의 상세 정보 링크"),
                                fieldWithPath("isContainerFriendly").description("용기 친화 식당 여부"),
                                fieldWithPath("isLike").description("로그인한 사용자의 피드 좋아요 여부"),
                                fieldWithPath("isScraped").description("로그인한 사용자의 피드 스크랩 여부")
                        )))
                .andDo(document("feed-list",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("create").description("새 피드를 생성하는 링크"),
                                linkWithRel("category-list").description("피드의 카테고리 분류"),
                                linkWithRel("next").description("다음 피드 리스트"),
                                linkWithRel("last").description("마지막 피드 리스트"),
                                linkWithRel("prev").description("이전 피드 리스트"),
                                linkWithRel("first").description("첫 피드 리스트")
                        ),
                        responseFields(
                                subsectionWithPath(LIST_PATH).description("피드 미리보기 리스트 (<<feed-preview,필드 참조>>)"),
                                subsectionWithPath("_links").description("본 응답에서 전이 가능한 링크 명세"),
                                fieldWithPath("page.size").description("본 피드 리스트의 페이지 당 피드 개수"),
                                fieldWithPath("page.totalElements").description("본 피드 리스트의 전체 피드 개수"),
                                fieldWithPath("page.totalPages").description("본 피드 리스트의 전체 페이지 수"),
                                fieldWithPath("page.number").description("본 피드 리스트에서 현재 페이지")

                        )));
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-user"));
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-restaurant"));
    }

    @Test
    @DisplayName("사용자가 스크랩한 피드 가져오기")
    public void testSelectUserScrapFeed() throws Exception {
        //given
        List<Feed> list = saveFeeds();
        Feed lastFeed = null;
        for (int i = 0; i < 10; i += 3) {
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
                .andExpect(jsonPath("_links.category-list.href").exists())
                .andDo(document("feed-by-scrap"));
    }

    @Test
    @DisplayName("추천 피드 가져오기")
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
    @DisplayName("카테고리 필터링과 정렬 테스트")
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
                                        "필터링할 카테고리 (대소문자 구분 X)"),
                                parameterWithName("sort").description(
                                        "정렬 방식 [createdDate|likeCount|difficulty],[ASC,DESC] +\n" +
                                                "기본값: createdDate,DESC"),
                                parameterWithName("size").description(
                                        "페이지당 피드 개수 +\n기본값: 20"),
                                parameterWithName("page").description(
                                        "가져올 페이지 +\n기본값: 0")
                        )));
    }

    @Test
    @DisplayName("피드 쓰기")
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
                                fieldWithPath("name").description("등록할 식당 이름"),
                                fieldWithPath("address").description("등록할 식당의 주소"),
                                fieldWithPath("latitude").description("등록할 식당 위치의 위도값"),
                                fieldWithPath("longitude").description("등록할 식당 위치의 경도값")
                        )))
                .andDo(document("feed-create",
                        requestFields(
                                subsectionWithPath("restaurantCreateDto")
                                        .description("등록할 식당의 식별값 (<<restaurant-register,필드 참조>>)"),
                                fieldWithPath("category").description("음식의 카테고리"),
                                subsectionWithPath("mainMenu").description("메인 음식 리스트"),
                                subsectionWithPath("subMenu").description("(Optional) 반찬 리스트"),
                                fieldWithPath("difficulty").description("포장 난이도"),
                                fieldWithPath("welcome").description("(Optional) 사장님 환영 여부"),
                                fieldWithPath("thumbnailImageId").description("(Optional) 썸네일 이미지 식별값"),
                                fieldWithPath("content").description("(Optional) 피드 콘텐트")
                        )));
    }

    @Test
    @DisplayName("피드 쓰기 실패")
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
    @DisplayName("피드 수정")
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
                                        .description("(Optional) 변경할 식당의 식별값 (<<restaurant-register,필드 참조>>)"),
                                fieldWithPath("category").description("(Optional) 음식의 카테고리"),
                                subsectionWithPath("mainMenu").description("(Optional) 메인 음식 리스트"),
                                subsectionWithPath("subMenu").description("(Optional) 반찬 리스트"),
                                fieldWithPath("difficulty").description("(Optional) 포장 난이도"),
                                fieldWithPath("welcome").description("(Optional) 사장님 환영 여부"),
                                fieldWithPath("thumbnailImageId").description("(Optional) 썸네일 이미지 식별값"),
                                fieldWithPath("content").description("(Optional) 피드 콘텐트")
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
    @DisplayName("피드 수정 실패")
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
    @DisplayName("피드 삭제")
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
            // 각 피드간에 생성시간차를 두기위해 잠시 대기
            Thread.sleep(0, 1);
        }
        return list;
    }
}