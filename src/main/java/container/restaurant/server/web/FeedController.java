package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.feed.recommend.RecommendFeedService;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import container.restaurant.server.web.linker.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;
    private final RecommendFeedService recommendFeedService;

    private final FeedLinker feedLinker;
    private final UserLinker userLinker;
    private final RestaurantLinker restaurantLinker;
    private final CommentLinker commentLinker;
    private final FeedLikeLinker feedLikeLinker;
    private final ScrapFeedLinker scrapFeedLinker;
    private final ReportLinker reportLinker;

    @GetMapping("{feedId}")
    public ResponseEntity<?> getFeedDetail(
            @PathVariable Long feedId, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.getFeedDetail(feedId, loginId), loginId));
    }

    @GetMapping
    public ResponseEntity<?> selectFeed(
            Pageable pageable, Category category, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAll(pageable, category, loginId)));
    }

    @GetMapping("recommend")
    public ResponseEntity<?> selectRecommend(@LoginId Long loginId) {
        CollectionModel<FeedPreviewDto> recommendFeeds = CollectionModel.of(
                recommendFeedService.findRecommends(loginId));
        if (recommendFeeds.getLink("self").isEmpty()) {
            setLinks(recommendFeeds);
        }
        return ResponseEntity.ok(recommendFeeds);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> selectUserFeed(
            @PathVariable Long userId, Pageable pageable, Category category, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByUser(userId, loginId, pageable, category)));
    }

    @GetMapping("user/{userId}/scrap")
    public ResponseEntity<?> selectUserScrapFeed(
            @PathVariable Long userId, Pageable pageable, Category category, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByUserScrap(userId, loginId, pageable, category)));
    }

    @GetMapping("restaurant/{restaurantId}")
    public ResponseEntity<?> selectRestaurantFeed(
            @PathVariable Long restaurantId, Pageable pageable, Category category, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByRestaurant(restaurantId, loginId, pageable, category)));
    }

    @PostMapping
    public ResponseEntity<?> createFeed(
            @Valid @RequestBody FeedInfoDto dto, @LoginId Long loginId
    ) {
        Long newFeedId = feedService.createFeed(dto, loginId);

        return ResponseEntity
                .created(feedLinker.getFeedDetail(newFeedId).toUri())
                .build();
    }

    @PatchMapping("{feedId}")
    public ResponseEntity<?> updateFeed(
            @Valid @RequestBody FeedInfoDto dto, @LoginId Long loginId,
            @PathVariable Long feedId
    ) {
        feedService.updateFeed(feedId, dto, loginId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> deleteFeed(
            @LoginId Long loginId, @PathVariable Long feedId
    ) {
        feedService.delete(feedId, loginId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("category")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(
                Arrays.stream(Category.values())
                        .collect(Collectors.toMap(
                                category -> category,
                                Category::getKorean)));
    }

    // TODO Linker 모듈에 setLinks 자체의 책임을 주는 방법을 고민해보자
    private FeedDetailDto setLinks(FeedDetailDto dto, Long loginId) {
        boolean isOwner = dto.getOwnerId().equals(loginId);
        return dto
                .add(
                        feedLinker.getFeedDetail(dto.getId()).withSelfRel(),
                        userLinker.getUserById(dto.getOwnerId()).withRel("owner"),
                        restaurantLinker.findById(dto.getRestaurantId()).withRel("restaurant"),
                        commentLinker.getCommentByFeed(dto.getId()).withRel("comments"),
                        dto.getIsLike() ?
                                feedLikeLinker.userCancelLikeFeed(dto.getId()).withRel("like-cancel") :
                                feedLikeLinker.userLikeFeed(dto.getId()).withRel("like"),
                        dto.getIsScraped() ?
                                scrapFeedLinker.cancelScrapFeed(dto.getId()).withRel("scrap-cancel") :
                                scrapFeedLinker.scrapFeed(dto.getId()).withRel("scrap")
                )
                .addAllIf(isOwner, () -> List.of(
                        feedLinker.updateFeed(dto.getId()).withRel("patch"),
                        feedLinker.deleteFeed(dto.getId()).withRel("delete")
                ))
                .addAllIf(!isOwner, () -> List.of(
                        reportLinker.reportFeed(dto.getId()).withRel("report")
                ));
    }

    private CollectionModel<FeedPreviewDto> setLinks(CollectionModel<FeedPreviewDto> list) {
        list.forEach(dto ->
                dto.add(feedLinker.getFeedDetail(dto.getId()).withSelfRel()));
        list.add(
                feedLinker.createFeed().withRel("create"),
                feedLinker.getCategoryList().withRel("category-list"));
        return list;
    }

}
