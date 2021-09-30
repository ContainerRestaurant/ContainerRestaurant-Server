package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendFeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;

    private List<RecommendFeed> recommendFeeds = List.of();

    private final static int DEFAULT_PAGE_SIZE = 1000;
    private final static Pageable DEFAULT_PAGEABLE = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    private Pageable pageable = null;

    @Autowired
    public RecommendFeedService(
            FeedRepository feedRepository, FeedLikeRepository feedLikeRepository
    ) {
        this.feedRepository = feedRepository;
        this.feedLikeRepository = feedLikeRepository;
    }

    RecommendFeedService(
            FeedRepository feedRepository, FeedLikeRepository feedLikeRepository,
            List<RecommendFeed> initCollection
    ) {
        this.feedRepository = feedRepository;
        this.feedLikeRepository = feedLikeRepository;
        this.recommendFeeds = initCollection;
    }

    public Collection<FeedPreviewDto> findRecommends(Long loginId) {
        List<Long> recommendFeedIds = recommendFeeds.stream()
                .map(RecommendFeed::getId)
                .collect(Collectors.toList());

        Set<Long> likeIdSet = loginId == null ? Set.of() :
                feedLikeRepository.checkFeedLikeOnIdList(loginId, recommendFeedIds);

        return recommendFeeds.stream()
                .map(recommendFeed -> FeedPreviewDto.from(
                        recommendFeed,
                        likeIdSet.contains(recommendFeed.getId())))
                .collect(Collectors.toList());
    }

    public void checkAndUpdate(Feed feed) {
        if (feed == null) return;

        recommendFeeds.stream()
                .filter(recommendFeed -> Objects.equals(recommendFeed.getId(), feed.getId()))
                .findFirst()
                .ifPresent(recommendFeed -> recommendFeed.update(feed));
    }

    public void checkAndDelete(Feed feed) {
        if (feed == null) return;

        recommendFeeds.stream()
                .filter(recommendFeed -> Objects.equals(recommendFeed.getId(), feed.getId()))
                .findFirst()
                .ifPresent(recommendFeeds::remove);
    }

    @PostConstruct
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional(readOnly = true)
    public void updateRecommendFeed() {
        RecommendFeedQueue queue = new RecommendFeedQueue();
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime from = to.minusWeeks(1);
        to = to.minusNanos(1);

        Pageable p = getPageable();
        Page<Feed> page = feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, p);
        while (page.hasContent()) {
            queue.addAll(page.getContent());
            p = p.next();
            page = feedRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, p);
        }
        recommendFeeds = queue.recommendFeedsTo(ArrayList::new, ArrayList::add);
    }

    public void setPageSize(int pageSize) {
        pageable = PageRequest.of(0, pageSize);
    }
    private Pageable getPageable() {
        return pageable == null ? DEFAULT_PAGEABLE : pageable;
    }

}
