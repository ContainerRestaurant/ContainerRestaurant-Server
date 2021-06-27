package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendFeedServiceTest extends BaseServiceTest {

    @InjectMocks
    RecommendFeedService recommendFeedService;

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource
    void updateRecommendFeed(String TEST, List<Feed> input, List<Long> res) {
        //given-1 기본날짜 / Pageable 이 주어지고 job 의 배치 페이지 사이즈를 세팅
        int pageSize = 10;
        recommendFeedService.setPageSize(pageSize);

        //given-2 주어진 날짜 사이에 모든 피드를 가져오는 로직을 목
        when(feedService.findForUpdatingRecommend(any(), any(), any()))
                .then(answer((LocalDateTime ign, LocalDateTime ore, Pageable p) -> {
                    int from = p.getPageSize() * p.getPageNumber();
                    if (from >= input.size()) return Page.empty(p);
                    int to = Math.min(from + p.getPageSize(), input.size());
                    return new PageImpl<>(input.subList(from, to), p, input.size());
                }));

        when(feedService.createFeedPreviewDto(any(), any()))
                .then(answer((Feed feed, Long loginId) -> FeedPreviewDto.builder()
                        .feed(feed)
                        .isLike(feedLikeRepository.existsByUserIdAndFeedId(loginId, feed.getId()))
                        .isScraped(scrapFeedRepository.existsByUserIdAndFeedId(loginId, feed.getId()))
                        .build()));
        //when 추천 업데이트 작업을 실행하고 결과 리스트를 ID 로 리스트로 변환
        recommendFeedService.updateRecommendFeed();
        List<Long> actualRes = recommendFeedService.getRecommendFeeds(null)
                .getContent().stream()
                .map(FeedPreviewDto::getId)
                .collect(Collectors.toList());

        //then 생성된 추천 피드 ID 리스트(mockRes) 와 예상한 res 가 동일하다.
        assertThat(actualRes).isEqualTo(res);
    }

    static Stream<Arguments> updateRecommendFeed() {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return Stream.of(
                arguments("스코어가 다른 경우 테스트", List.of(
                        testFeed(0L, 1, today),
                        testFeed(3L, 4, today),
                        testFeed(12L, 13, today),
                        testFeed(13L, 14, today),
                        testFeed(1L, 2, today),
                        testFeed(2L, 3, today),
                        testFeed(14L, 15, today),
                        testFeed(15L, 16, today),
                        testFeed(4L, 5, today),
                        testFeed(5L, 6, today),
                        testFeed(8L, 9, today),
                        testFeed(9L, 10, today),
                        testFeed(10L, 11, today),
                        testFeed(6L, 7, today),
                        testFeed(7L, 8, today),
                        testFeed(11L, 12, today),
                        testFeed(16L, 17, today),
                        testFeed(17L, 18, today)
                ), List.of(
                        17L, 16L, 15L, 14L,
                        13L, 12L, 11L, 10L,
                        9L, 8L, 7L, 6L
                )),
                arguments("날짜가 다른 경우 테스트", List.of(
                        testFeed(9L, 3, today.minusDays(1)),
                        testFeed(1L, 1, today.plusDays(1)),
                        testFeed(2L, 1, today.plusDays(2)),
                        testFeed(13L, 4, today.minusDays(2)),
                        testFeed(14L, 4, today.minusDays(1)),
                        testFeed(15L, 4, today),
                        testFeed(5L, 2, today),
                        testFeed(0L, 1, today),
                        testFeed(7L, 2, today.plusDays(2)),
                        testFeed(8L, 3, today.minusDays(2)),
                        testFeed(6L, 2, today.plusDays(1)),
                        testFeed(16L, 4, today.plusDays(1)),
                        testFeed(11L, 3, today.plusDays(1)),
                        testFeed(12L, 3, today.plusDays(2)),
                        testFeed(3L, 2, today.minusDays(2)),
                        testFeed(4L, 2, today.minusDays(1)),
                        testFeed(10L, 3, today),
                        testFeed(17L, 4, today.plusDays(2))
                ), List.of(
                        17L, 16L, 15L, 14L,
                        13L, 12L, 11L, 10L,
                        9L, 8L, 7L, 6L
                ))
        );
    }

    static Feed testFeed(Long id, Integer score, LocalDateTime createDate) {
        Feed feed = mock(Feed.class);
        User user = mock(User.class);
        Image image = mock(Image.class);
        Restaurant restaurant = mock(Restaurant.class);
        when(feed.getId()).thenReturn(id);
        when(feed.getRecommendScore()).thenReturn(score);
        when(feed.getCreatedDate()).thenReturn(createDate);
        when(feed.getOwner()).thenReturn(user);
        when(feed.getThumbnail()).thenReturn(image);
        when(feed.getRestaurant()).thenReturn(restaurant);
        when(image.getUrl()).thenReturn("test.path");
        when(restaurant.isContainerFriendly()).thenReturn(false);
        return feed;
    }
}