package container.restaurant.server.web;

import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.scrap.ScrapFeed;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.domain.user.scrap.ScrapFeedService;
import container.restaurant.server.web.base.BaseUserAndFeedControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ScrapFeedControllerTest extends BaseUserAndFeedControllerTest {

    @Autowired
    private ScrapFeedRepository scrapFeedRepository;

    @Autowired
    private ScrapFeedService scrapFeedService;
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;

    @AfterEach
    public void afterEach() {
        scrapFeedRepository.deleteAll();
        super.afterEach();
    }

    @Test
    @DisplayName("피드 스크랩하기")
    void scrapFeed() throws Exception {
        //given other 유저가 작성한 피드가 주어졌을 때

        //when myself 유저 세션으로 주어진 피드를 스크랩하면
        mvc.perform(
                post("/api/scrap/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, cancel-scrap 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.scrap-cancel.href").exists());

        //then-2 myself , otherFeed 의 스크랩 개수가 + 1 되고,
        //       myself 와 주어진 피드가 관계된 하나의 FeedScrap 이 존재한다.
        assertThat(userService.findById(myself.getId()).getScrapCount())
                .isEqualTo(myself.getScrapCount() + 1);
        assertThat(feedService.findById(othersFeed.getId()).getScrapCount())
                .isEqualTo(othersFeed.getScrapCount() + 1);

        Page<ScrapFeed> scraps = scrapFeedRepository.findAllByUserId(myself.getId(), Pageable.unpaged());
        List<ScrapFeed> scrapList = scraps.getContent();
        assertThat(scrapList.size()).isEqualTo(1);

        ScrapFeed scrap = scrapList.get(0);
        assertThat(scrap.getUser().getId()).isEqualTo(myself.getId());
        assertThat(scrap.getFeed().getId()).isEqualTo(othersFeed.getId());
    }

    @Test
    @DisplayName("중복되는 피드 스크랩하기")
    void failScrapFeed() throws Exception {
        //given 유저가 이미 스크랩한 피드와 when 동작 전 시간이 주어졌을 때
        scrapFeedService.userScrapFeed(myself.getId(), othersFeed.getId());
        myself = userRepository.findById(myself.getId())
                .orElse(myself);
        othersFeed = feedRepository.findById(othersFeed.getId())
                .orElse(othersFeed);
        Thread.sleep(0, 1);
        LocalDateTime now = LocalDateTime.now();

        //when myself 유저 세션으로 주어진 피드를 스크랩하면
        mvc.perform(
                post("/api/scrap/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, cancel-scrap 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.scrap-cancel.href").exists())
                .andDo(document("feed-scrap",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("scrap-cancel").description("본 스크랩을 취소하는 링크")
                        )
                ));

        //then-2 myself, otherFeed 의 스크랩 개수가 그대로이고,
        //       이미 만들어져있던 FeedScrap 이 존재한다.
        assertThat(userService.findById(myself.getId()).getScrapCount())
                .isEqualTo(myself.getScrapCount());
        assertThat(feedService.findById(othersFeed.getId()).getScrapCount())
                .isEqualTo(othersFeed.getScrapCount());

        Page<ScrapFeed> scraps = scrapFeedRepository.findAllByUserId(myself.getId(), Pageable.unpaged());
        List<ScrapFeed> scrapList = scraps.getContent();
        assertThat(scrapList.size()).isEqualTo(1);

        ScrapFeed scrap = scrapList.get(0);
        assertThat(scrap.getCreatedDate()).isBefore(now);
    }

    // FIXME 임시 로그인 방편
//    @Test
//    @DisplayName("인증되지 않은 유저의 스크랩 실패")
//    void failUnauthenticatedUser() throws Exception {
//        //given other 유저가 작성한 피드가 주어졌을 때
//
//        //then 인증되지 않은 유저가 주어진 피드를 스크랩하면 login 리다이렉트
//        mvc.perform(post("/api/scrap/{feedId}", othersFeed.getId()))
//                .andExpect(status().isFound());
//    }

    @Test
    @DisplayName("스크랩 취소")
    void cancelScrapFeed() throws Exception {
        //given 유저가 이미 스크랩한 피드가 주어졌을 때
        scrapFeedService.userScrapFeed(myself.getId(), othersFeed.getId());
        myself = userRepository.findById(myself.getId())
                .orElse(myself);
        othersFeed = feedRepository.findById(othersFeed.getId())
                .orElse(othersFeed);

        //when myself 유저 세션으로 주어진 피드를 스크랩을 삭제하면
        mvc.perform(
                delete("/api/scrap/{feedId}", othersFeed.getId())
                        .session(myselfSession))
                //then-1 status 200 에 self, scrap 링크가 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.scrap.href").exists())
                .andDo(document("feed-scrap-cancel",
                        links(
                                linkWithRel("self").description("본 응답의 링크"),
                                linkWithRel("scrap").description("다시 스크랩하는 링크")
                        )
                ));

        //then myself, otherFeed 의 스크랩 개수가 1개 줄어들고, 남은 FeedScrap 없어진다.
        assertThat(userService.findById(myself.getId()).getScrapCount())
                .isEqualTo(myself.getScrapCount() - 1);
        assertThat(feedService.findById(othersFeed.getId()).getScrapCount())
                .isEqualTo(othersFeed.getScrapCount()- 1);

        Page<ScrapFeed> scraps = scrapFeedRepository.findAllByUserId(myself.getId(), Pageable.unpaged());
        List<ScrapFeed> scrapList = scraps.getContent();
        assertThat(scrapList.size()).isEqualTo(0);
    }

}