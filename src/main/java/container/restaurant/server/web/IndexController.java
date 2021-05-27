package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginUser;
import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.domain.banner.BannerService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.IndexDto;
import container.restaurant.server.web.linker.BannerLinker;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.StatisticsLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class IndexController {

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;
    private final StatisticsLinker statisticsLinker;
    private final BannerLinker bannerLinker;

    private final UserService userService;
    private final StatisticsService statisticsService;
    private final BannerService bannerService;

    // TODO 메인 문구 랜덤 생성

    @GetMapping
    public ResponseEntity<?> index(@LoginUser SessionUser sessionUser) {
        User loginUser = sessionUser != null ? userService.findById(sessionUser.getId()) : null;

        return ResponseEntity.ok(
                setLinks(IndexDto.builder()
                        .user(loginUser)
                        .totalContainer(statisticsService.getTotalFeed())
                        .build()));
    }

    private IndexDto setLinks(IndexDto model) {
        Long loginId = model.getLoginId();
        return model
                .add(List.of(
                        linkTo(IndexController.class).withSelfRel(),
                        feedLinker.selectFeed().withRel("feed-list"),
                        feedLinker.selectRecommend().withRel("feed-recommend"),
                        feedLinker.createFeed().withRel("feed-create"),
                        statisticsLinker.getFeedCountTopUsers().withRel("top-users"),
                        statisticsLinker.getRecentFeedUsers().withRel("recent-users"),
                        bannerLinker.getBanners().withRel("banner-list")
                ))
                .addAllIf(loginId == null, () -> List.of(
                        Link.of("/login").withRel("login"),
                        Link.of("/login").withRel("my-info")
                ))
                .addAllIf(loginId != null, () -> List.of(
                        Link.of("/logout").withRel("logout"),
                        userLinker.getUserById(loginId).withRel("my-info")
                ));
    }

    @GetMapping("/banners")
    public ResponseEntity<?> getBanners(){
        return ResponseEntity.ok(
                bannerService.getBanners()
        );
    }

}
