package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
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
            @PathVariable Long feedId, @LoginUser SessionUser sessionUser
    ) {
        Long loginId = sessionUser != null ? sessionUser.getId() : null;
        return ResponseEntity.ok(
                setLinks(feedService.getFeedDetail(feedId, loginId), loginId));
    }

    @GetMapping
    public ResponseEntity<?> selectFeed(Pageable pageable, Category category) {

        return ResponseEntity.ok(
                setLinks(feedService.findAll(pageable, category)));
    }

    @GetMapping("recommend")
    public ResponseEntity<?> selectRecommend() {
        return ResponseEntity.ok(
                setLinks(recommendFeedService.getRecommendFeeds()));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> selectUserFeed(
            @PathVariable Long userId, Pageable pageable, Category category
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByUser(userId, pageable, category)));
    }

    @GetMapping("user/{userId}/scrap")
    public ResponseEntity<?> selectUserScrapFeed(
            @PathVariable Long userId, Pageable pageable, Category category
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByUserScrap(userId, pageable, category)));
    }

    @GetMapping("restaurant/{restaurantId}")
    public ResponseEntity<?> selectRestaurantFeed(
            @PathVariable Long restaurantId, Pageable pageable, Category category
    ) {
        return ResponseEntity.ok(
                setLinks(feedService.findAllByRestaurant(restaurantId, pageable, category)));
    }

    @PostMapping
    public ResponseEntity<?> createFeed(
            @Valid @RequestBody FeedInfoDto dto, @LoginUser SessionUser sessionUser
    ) {
        Long newFeedId = feedService.createFeed(dto, sessionUser.getId());

        return ResponseEntity
                .created(feedLinker.getFeedDetail(newFeedId).toUri())
                .build();
    }

    @PatchMapping("{feedId}")
    public ResponseEntity<?> updateFeed(
            @Valid @RequestBody FeedInfoDto dto, @LoginUser SessionUser sessionUser,
            @PathVariable Long feedId
    ) {
        feedService.updateFeed(feedId, dto, sessionUser.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> deleteFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        feedService.delete(feedId, sessionUser.getId());
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
