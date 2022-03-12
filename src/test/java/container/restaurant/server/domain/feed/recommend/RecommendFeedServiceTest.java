package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecommendFeedServiceTest extends BaseMockTest {

    @Mock
    FeedRepository feedRepository;
    @Mock
    FeedLikeRepository feedLikeRepository;

    @InjectMocks
    RecommendFeedService service;

    @Test
    @DisplayName("추천 피드이면 업데이트 - 추천 피드에 존재하는 경우")
    void checkAndUpdate() {
        //given 추천 피드 리스트에 주어진 피드 ID 를 가지는 추천피드가 있을 때
        Feed feed = mock(Feed.class);
        when(feed.getId()).thenReturn(1L);

        List<RecommendFeed> recommendFeeds = List.of(
                newRecommendFeed(1L), newRecommendFeed(2L),
                newRecommendFeed(3L), newRecommendFeed(4L));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when 주어진 피드로 checkAndUpdate() 를 콜하면
        listInjectedService.checkAndUpdate(feed);

        //then ID 가 일치하는 추천 피드에서만 주어진 피드로 update() 가 호출됨
        for (RecommendFeed recommendFeed : recommendFeeds) {
            if (Objects.equals(recommendFeed.getId(), feed.getId()))
                verify(recommendFeed).update(feed);
            else
                verify(recommendFeed, never()).update(any());
        }
    }

    @Test
    @DisplayName("추천 피드이면 업데이트 - 추천 피드에 존재하지 않는 경우")
    void checkAndUpdate__absent() {
        //given 추천 피드 리스트에 주어진 피드 ID 를 가지는 추천피드가 없을 때
        Feed feed = mock(Feed.class);
        when(feed.getId()).thenReturn(5L);

        List<RecommendFeed> recommendFeeds = List.of(
                newRecommendFeed(1L), newRecommendFeed(2L),
                newRecommendFeed(3L), newRecommendFeed(4L));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when 주어진 피드로 checkAndUpdate() 를 콜하면
        listInjectedService.checkAndUpdate(feed);

        //then 주어진 피드로 update() 가 호출되지 않음
        for (RecommendFeed recommendFeed : recommendFeeds)
            verify(recommendFeed, never()).update(any());
    }

    @Test
    @DisplayName("추천 피드이면 업데이트 - 피드가 null")
    void checkAndUpdate__null() {
        //given 주어진 피드가 없을 때
        List<RecommendFeed> recommendFeeds = List.of(
                newRecommendFeed(1L), newRecommendFeed(2L),
                newRecommendFeed(3L), newRecommendFeed(4L));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when null 피드로 checkAndUpdate() 를 콜하면
        listInjectedService.checkAndUpdate(null);

        //then 주어진 피드로 update() 가 호출되지 않음
        for (RecommendFeed recommendFeed : recommendFeeds)
            verify(recommendFeed, never()).update(any());
    }

    @Test
    @DisplayName("추천 피드 삭제 - 정상")
    void checkAndDelete() {
        //given
        Feed feed = mock(Feed.class);
        when(feed.getId()).thenReturn(1L);

        RecommendFeed del = newRecommendFeed(1L);
        List<RecommendFeed> recommendFeeds = spy(new ArrayList<>(List.of(
                del, newRecommendFeed(2L))));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when
        listInjectedService.checkAndDelete(feed);

        //then
        verify(recommendFeeds).remove(del);
    }

    @Test
    @DisplayName("추천 피드 삭제 - 존재하지 않는 피드")
    void checkAndDelete_noExists() {
        //given
        Feed feed = mock(Feed.class);
        when(feed.getId()).thenReturn(0L);

        List<RecommendFeed> recommendFeeds = spy(new ArrayList<>(List.of(
                newRecommendFeed(1L), newRecommendFeed(2L))));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when
        listInjectedService.checkAndDelete(feed);

        //then
        verify(recommendFeeds, never()).remove(any(RecommendFeed.class));
    }

    @Test
    @DisplayName("추천 피드 삭제 - null")
    void checkAndDelete_null() {
        //given
        List<RecommendFeed> recommendFeeds = spy(new ArrayList<>(List.of(
                newRecommendFeed(1L), newRecommendFeed(2L))));

        RecommendFeedService listInjectedService = new RecommendFeedService(
                feedRepository, feedLikeRepository, recommendFeeds);

        //when
        listInjectedService.checkAndDelete(null);

        //then
        verify(recommendFeeds, never()).remove(any(RecommendFeed.class));
    }

    @ParameterizedTest(name = "추천 점수에 따른 리스트 순서 테스트 [{index}] - {0}")
    @MethodSource
    void findRecommends(String TEST, List<Feed> input, List<Long> res) {
        //given-1 기본날짜 / Pageable 이 주어지고 job 의 배치 페이지 사이즈를 세팅
        int pageSize = 10;
        service.setPageSize(pageSize);

        //given-2 주어진 날짜 사이에 모든 피드를 가져오는 로직을 목
        when(feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(any(), any(), any()))
                .then(answer((LocalDateTime ign, LocalDateTime ore, Pageable p) -> {
                    int from = p.getPageSize() * p.getPageNumber();
                    if (from >= input.size()) return Page.empty(p);
                    int to = Math.min(from + p.getPageSize(), input.size());
                    return new PageImpl<>(input.subList(from, to), p, input.size());
                }));

        when(feedLikeRepository.checkFeedLikeOnIdList(any(), any())).thenReturn(Set.of());

        //when 추천 업데이트 작업을 실행하고 결과 리스트를 ID 로 리스트로 변환
        service.updateRecommendFeed();
        List<Long> actualRes = service.findRecommends(null).stream()
                .map(FeedPreviewDto::getId)
                .collect(Collectors.toList());

        //then 생성된 추천 피드 ID 리스트(mockRes) 와 예상한 res 가 동일하다.
        assertThat(actualRes).isEqualTo(res);
    }

    static Stream<Arguments> findRecommends() {
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

    @NotNull
    private RecommendFeed newRecommendFeed(long l) {
        RecommendFeed recommendFeed1 = mock(RecommendFeed.class);
        when(recommendFeed1.getId()).thenReturn(l);
        return recommendFeed1;
    }
}