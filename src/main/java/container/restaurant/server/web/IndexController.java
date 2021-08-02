package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.home.banner.BannerService;
import container.restaurant.server.domain.home.phrase.PhraseService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.IndexDto;
import container.restaurant.server.web.linker.*;
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
    private final IndexLinker indexLinker;
    private final RestaurantLinker restaurantLinker;

    private final UserService userService;
    private final StatisticsService statisticsService;
    private final BannerService bannerService;
    private final PhraseService phraseService;

    @GetMapping
    public ResponseEntity<?> index(@LoginId Long loginId) {
        User loginUser = loginId != null ? userService.findById(loginId) : null;

        return ResponseEntity.ok(
                setLinks(IndexDto.builder()
                        .user(loginUser)
                        .totalContainer(statisticsService.getTotalFeed())
                        .phrase(phraseService.getPhrase())
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
                        indexLinker.getBanners().withRel("banner-list"),
                        restaurantLinker.findNearByRestaurants().withRel("restaurant-near")
                ))
                .addAllIf(loginId == null, () -> List.of(
                        Link.of("/login").withRel("login")
                ))
                .addAllIf(loginId != null, () -> List.of(
                        Link.of("/logout").withRel("logout"),
                        userLinker.getCurrentUser().withRel("my-info")
                ));
    }

    @GetMapping("/banners")
    public ResponseEntity<?> getBanners(){
        return ResponseEntity.ok(
                bannerService.getBanners()
        );
    }

}
