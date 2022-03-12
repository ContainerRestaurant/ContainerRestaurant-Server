package container.restaurant.server.web;

import container.restaurant.server.config.auth.LoginId;
import container.restaurant.server.domain.home.banner.BannerRepository;
import container.restaurant.server.domain.home.phrase.PhraseService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.HomeDto;
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
@RequestMapping("api/home")
public class HomeController {

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;
    private final StatisticsLinker statisticsLinker;
    private final BannerLinker bannerLinker;
    private final RestaurantLinker restaurantLinker;

    private final UserService userService;
    private final StatisticsService statisticsService;
    private final PhraseService phraseService;

    private final BannerRepository bannerRepository;

    @GetMapping
    public ResponseEntity<?> home(@LoginId Long loginId) {
        User loginUser = loginId != null ? userService.findById(loginId) : null;

        return ResponseEntity.ok(
                setLinks(HomeDto.builder()
                        .user(loginUser)
                        .totalContainer(statisticsService.getTotalFeedCount())
                        .phrase(phraseService.getPhrase())
                        .latestWriters(statisticsService.getLatestWriters())
                        .banners(bannerRepository.findAllHomeBanner())
                        .build()));
    }

    private HomeDto setLinks(HomeDto model) {
        Long loginId = model.getLoginId();
        return model
                .add(List.of(
                        linkTo(HomeController.class).withSelfRel(),
                        feedLinker.selectFeed().withRel("feed-list"),
                        feedLinker.selectRecommend().withRel("feed-recommend"),
                        feedLinker.createFeed().withRel("feed-create"),
                        bannerLinker.getBanners().withRel("banner-list"),
                        restaurantLinker.findNearByRestaurants().withRel("restaurant-near"),
                        statisticsLinker.getFeedStatistics().withRel("total-container")
                ))
                .addAllIf(loginId == null, () -> List.of(
                        Link.of("/login").withRel("login")
                ))
                .addAllIf(loginId != null, () -> List.of(
                        Link.of("/logout").withRel("logout"),
                        userLinker.getCurrentUser().withRel("my-info")
                ));
    }

}
