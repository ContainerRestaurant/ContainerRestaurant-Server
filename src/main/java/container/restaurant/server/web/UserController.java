package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.comment.like.CommentLikeService;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.feed.like.FeedLikeService;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.report.ReportService;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.scrap.ScrapFeedService;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.exception.FailedAuthorizationException;
import container.restaurant.server.web.dto.user.UserDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.RestaurantFavoriteLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final FeedService feedService;
    private final CommentLikeService commentLikeService;
    private final CommentService commentService;
    private final FeedLikeService feedLikeService;
    private final ScrapFeedService scrapFeedService;
    private final ReportService reportService;

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;
    private final RestaurantFavoriteLinker restaurantFavoriteLinker;

    @PostMapping
    public ResponseEntity<UserDto.Token> tokenRequest(@RequestBody UserDto.ToRequestToken dto) {
        return ResponseEntity.ok(userService.newToken(dto));
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@LoginId Long loginId) {
        return ResponseEntity.of(ofNullable(loginId)
                .map(id -> setLinks(userService.getUserInfoById(id), id)));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @LoginId Long loginId
    ) {
        return ResponseEntity.ok(
                setLinks(userService.getUserInfoById(id), loginId)
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id, @LoginId Long loginId,
            @RequestBody UserDto.Update updateDto
    ) {
        if (!id.equals(loginId))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        return ResponseEntity.ok(
                setLinks(userService.update(id, updateDto), id)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Long id, @LoginId Long loginId
    ) {
        if (!id.equals(loginId))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");

        //report 삭제
        reportService.deleteAllByReporterId(id);
        //tb_feed 삭제
        List<Long> userFeedIdList = feedService.findAllByOwnerId(id);
        for (Long userFeedId : userFeedIdList) {
            feedService.delete(userFeedId, id);
        }
        //tb_comment에서 해당 comment like_count-- 후 tb_comment_like 삭제
        commentLikeService.deleteAllByUserId(id);
        //tb_feed에서 reply_count--후 tb_comment 삭제
        commentService.deleteAllByOwnerId(id);
        // 해당 feed에서 like,scrap count--후 tb_feed_hit, tb_feed_like, tb_scrap_feed 삭제
        feedLikeService.deleteAllByUserId(id);
        scrapFeedService.deleteAllByUserId(id);
        //최종 사용자 및 테이블 데이터 삭제
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}/push/token")
    public ResponseEntity<PushToken> unregisterPushToken(
            @PathVariable Long id, @LoginId Long loginId) {
        if (!id.equals(loginId))
            throw new FailedAuthorizationException("해당 사용자의 정보를 수정할 수 없습니다.(id:" + id + ")");
        return ResponseEntity.ok(userService.unregisterPushToken(loginId));
    }

    @GetMapping("nickname/exists")
    public ResponseEntity<?> existsNickname(
            @NicknameConstraint @RequestParam String nickname
    ) {
        return ResponseEntity.ok(
                setLinks(UserDto.NicknameExists.of(nickname, userService.existsUserByNickname(nickname)))
        );
    }

    private UserDto.NicknameExists setLinks(UserDto.NicknameExists dto) {
        return dto.add(userLinker.existsNickname(dto.getNickname()).withSelfRel());
    }

    private UserDto.Info setLinks(UserDto.Info dto, Long loginId) {
        return dto
                .add(
                        userLinker.getUserById(dto.getId()).withSelfRel(),
                        feedLinker.selectUserFeed(dto.getId()).withRel("feeds")
                )
                .addAllIf(dto.getId().equals(loginId), () -> List.of(
                        userLinker.updateUserById(dto.getId()).withRel("patch"),
                        userLinker.deleteById(dto.getId()).withRel("delete"),
                        userLinker.existsNickname().withRel("nickname-exists"),
                        feedLinker.selectUserScrapFeed(dto.getId()).withRel("scraps"),
                        restaurantFavoriteLinker.findAllByUser().withRel("restaurant-favorite")
                ));
    }
}
