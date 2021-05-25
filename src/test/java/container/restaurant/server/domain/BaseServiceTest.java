package container.restaurant.server.domain;

import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.comment.CommentService;
import container.restaurant.server.domain.comment.like.CommentLikeRepository;
import container.restaurant.server.domain.comment.like.CommentLikeService;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.domain.feed.container.ContainerRepository;
import container.restaurant.server.domain.feed.container.ContainerService;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.like.FeedLikeService;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.report.ReportCommentRepository;
import container.restaurant.server.domain.report.ReportFeedRepository;
import container.restaurant.server.domain.report.ReportService;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteRepository;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteService;
import container.restaurant.server.domain.restaurant.menu.MenuRepository;
import container.restaurant.server.domain.restaurant.menu.MenuService;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.bookmark.FavoriteRestaurantRepository;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.domain.user.scrap.ScrapFeedService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PagedResourcesAssembler;

public class BaseServiceTest {

    @BeforeEach
    void setMock() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------COMMENT DOMAIN--------------------
    @Mock
    protected CommentLikeRepository commentLikeRepository;
    @Mock
    protected CommentLikeService commentLikeService;
    @Mock
    protected CommentRepository commentRepository;
    @Mock
    protected CommentService commentService;

    // --------------------FEED DOMAIN--------------------
    @Mock
    protected ContainerRepository containerRepository;
    @Mock
    protected ContainerService containerService;
    @Mock
    protected FeedHitRepository feedHitRepository;
    @Mock
    protected FeedLikeRepository feedLikeRepository;
    @Mock
    protected FeedLikeService feedLikeService;
    @Mock
    protected ImageRepository imageRepository;
    @Mock
    protected ImageService imageService;
    @Mock
    protected FeedRepository feedRepository;
    @Mock
    protected FeedService feedService;

    // --------------------REPORT--------------------
    @Mock
    protected ReportCommentRepository reportCommentRepository;
    @Mock
    protected ReportFeedRepository reportFeedRepository;
    @Mock
    protected ReportService reportService;

    // --------------------RESTAURANT--------------------
    @Mock
    protected RestaurantFavoriteRepository restaurantFavoriteRepository;
    @Mock
    protected RestaurantFavoriteService restaurantFavoriteService;
    @Mock
    protected MenuRepository menuRepository;
    @Mock
    protected MenuService menuService;
    @Mock
    protected RestaurantRepository restaurantRepository;
    @Mock
    protected RestaurantService restaurantService;

    // --------------------USER--------------------
    @Mock
    protected FavoriteRestaurantRepository favoriteRestaurantRepository;
    @Mock
    protected ScrapFeedRepository scrapFeedRepository;
    @Mock
    protected ScrapFeedService scrapFeedService;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected UserService userService;

    // --------------------USER--------------------
    @Mock
    protected StatisticsService statisticsService;

    @Mock
    protected PagedResourcesAssembler<Feed> feedAssembler;
}
