package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.UserLinker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * TODO 필터 조건
 *  - 카테고리 필터링
 *
 * TODO 정렬 조건
 *  - 최신 순
 *  - 좋아요 많은 순
 *  - 난이도 쉽/어렵 순
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    private final FeedLinker feedLinker;
    private final UserLinker userLinker;

    @GetMapping("{feedId}")
    public ResponseEntity<?> getFeedDetail(
            @PathVariable Long feedId, @LoginUser SessionUser sessionUser
    ) {
        Long loginId = sessionUser != null ? sessionUser.getId() : -1;
        return ResponseEntity.ok(
                setLinks(feedService.getFeedDetail(feedId), loginId));
    }

    @GetMapping
    public ResponseEntity<?> selectFeed(Pageable pageable) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @GetMapping("recommend")
    public ResponseEntity<?> selectRecommend() {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> selectUserFeed(
            @PathVariable Long userId, Pageable pageable
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @GetMapping("user/{userId}/scrap")
    public ResponseEntity<?> selectUserScrapFeed(
            @PathVariable Long userId, Pageable pageable
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @GetMapping("restaurant/{restaurantId}")
    public ResponseEntity<?> selectRestaurantFeed(
            @PathVariable Long restaurantId, Pageable pageable
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createFeed(
            @LoginUser SessionUser sessionUser
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity<?> deleteFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("{feedId}")
    public ResponseEntity<?> updateFeed(
            @LoginUser SessionUser sessionUser, @PathVariable Long feedId
    ) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    private FeedDetailDto setLinks(FeedDetailDto dto, Long loginId) {
        return dto
                .add(
                        feedLinker.getFeedDetail(dto.getId()).withSelfRel(),
                        userLinker.getUserById(dto.getOwnerId()).withRel("owner")
                        // TODO restaurant link
                        // TODO comment link
                )
                .addAllIf(loginId.equals(dto.getOwnerId()), () -> List.of(
                        feedLinker.updateFeed(dto.getId()).withRel("patch"),
                        feedLinker.deleteFeed(dto.getId()).withRel("delete")
                ));
    }

}
