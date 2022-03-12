package container.restaurant.server.domain.feed;

import container.restaurant.server.BaseMockTest;
import container.restaurant.server.domain.comment.CommentRepository;
import container.restaurant.server.domain.feed.hit.FeedHitRepository;
import container.restaurant.server.domain.feed.like.FeedLikeRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.feed.recommend.RecommendFeedService;
import container.restaurant.server.domain.report.ReportFeedRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantService;
import container.restaurant.server.domain.restaurant.menu.Menu;
import container.restaurant.server.domain.statistics.StatisticsService;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.domain.user.level.UserLevelFeedCountService;
import container.restaurant.server.domain.user.scrap.ScrapFeedRepository;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedMenuDto;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FeedServiceTest extends BaseMockTest {

    private static final int MENU_NUM = 3;

    @Mock
    FeedRepository feedRepository;
    @Mock
    UserService userService;
    @Mock
    RestaurantService restaurantService;
    @Mock
    ImageService imageService;
    @Mock
    UserLevelFeedCountService userLevelFeedCountService;
    @Mock
    FeedHitRepository feedHitRepository;
    @Mock
    RecommendFeedService recommendFeedService;
    @Mock
    FeedLikeRepository feedLikeRepository;
    @Mock
    ScrapFeedRepository scrapFeedRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    StatisticsService statisticsService;
    @Mock
    ReportFeedRepository reportFeedRepository;

    @Spy
    @InjectMocks
    private Feed feed;

    @Mock
    private User user;
    @Mock
    private Restaurant restaurant;
    @Mock
    private Image thumbnail;

    private List<Container> mainMenus;
    private List<Container> subMenus;

    @InjectMocks
    private FeedService feedService;

    @Captor
    ArgumentCaptor<Feed> feedCaptor;

    @Test
    @DisplayName("Container 를 포함해 단일 Feed 찾기 테스트")
    void findByIdWithContainers() {
        //given findByIdWithContainers() 목 데이터
        mockFindFeedByIdWithContainers();

        //when 함수를 콜하면
        Feed actualFeed = feedService.findById(feed.getId());

        //then 기대값과 결과값이 일치한다.
        assertThat(actualFeed).isEqualTo(feed);
    }

    @Test
    @DisplayName("Feed 상세 DTO 반환 테스트")
    void getFeedDetail() {
        //given findByIdWithContainers() 목 데이터
        mockFindFeedByIdWithContainers();

        //when 함수를 콜하면
        FeedDetailDto actualDto = feedService.getFeedDetail(feed.getId(), null);

        //then 생성된 DTO 에 관련된 엔티티들의 정보가 정상적으로 포함되어있다.
        assertThat(actualDto.getId()).isEqualTo(feed.getId());
        assertThat(actualDto.getOwnerId()).isEqualTo(user.getId());
        assertThat(actualDto.getRestaurantId()).isEqualTo(restaurant.getId());

        assertEqualsMenuAndDto(mainMenus, actualDto.getMainMenu());
        assertEqualsMenuAndDto(subMenus, actualDto.getSubMenu());
    }

    @Test
    @DisplayName("피드 생성 테스트")
    void createFeed() {
        // given
        when(feedRepository.save(any())).thenReturn(feed);
        when(userService.findById(user.getId())).thenReturn(user);
        when(restaurantService.findByDto(any())).thenReturn(restaurant);
        when(imageService.findById(any())).thenReturn(thumbnail);

        FeedMenuDto mainMenuDto = FeedMenuDto.builder()
                .menuName("mainMenu")
                .container("mainMenuContainer")
                .build();

        FeedMenuDto subMenuDto = FeedMenuDto.builder()
                .menuName("subMenu")
                .container("subMenuContainer")
                .build();

        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantCreateDto(RestaurantInfoDto.from(restaurant))
                .category(Category.FAST_FOOD)
                .mainMenu(List.of(mainMenuDto))
                .subMenu(List.of(subMenuDto))
                .difficulty(3)
                .welcome(true)
                .thumbnailImageId(thumbnail.getId())
                .content("this is new Feed")
                .build();

        //when 함수를 콜하면
        feedService.createFeed(dto, user.getId());

        //then FeedInfoDto 정보와 동등한 Feed, FeedMenuDto 정보와 동등한 Containers 가 save 된다.
        verify(userLevelFeedCountService).levelFeedUp(feed);
        verify(feedRepository).save(feedCaptor.capture());
        verify(restaurant).updateFeedStatics(feed);
        Feed saved = feedCaptor.getValue();
        assertThat(saved.getOwner()).isEqualTo(user);
        assertThat(saved.getRestaurant()).isEqualTo(restaurant);
        assertThat(saved.getCategory()).isEqualTo(dto.getCategory());
        assertThat(saved.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(saved.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(saved.getThumbnail().getId()).isEqualTo(dto.getThumbnailImageId());
        assertThat(saved.getContent()).isEqualTo(dto.getContent());

        assertThat(saved.getContainerList())
                .hasSize(2)
                .anyMatch(container -> equalsMenuAndDto(container, mainMenuDto))
                .anyMatch(container -> equalsMenuAndDto(container, subMenuDto));
    }

    @Test
    @DisplayName("피드 삭제 테스트")
    void deleteTest() {
        //given findByIdWithContainers() 목 데이터
        mockFindFeedByIdWithContainers();

        //when 함수를 콜하면
        feedService.delete(feed.getId(), user.getId());

        //then
        InOrder order = inOrder(feedHitRepository,reportFeedRepository, commentRepository, feedLikeRepository, feedRepository);
        verify(userLevelFeedCountService).levelFeedDown(feed);
        verify(recommendFeedService).checkAndDelete(feed);
        verify(restaurant).deleteFeedStatics(feed);
        order.verify(feedHitRepository).deleteAllByFeed(feed);
        order.verify(reportFeedRepository).deleteAllByFeedId(feed.getId());
        order.verify(commentRepository).deleteAllByFeed(feed);
        order.verify(feedLikeRepository).deleteAllByFeed(feed);
        order.verify(feedRepository).delete(feed);
    }

    @Test
    @DisplayName("피드 수정 테스트")
    void updateFeed() {
        //given findByIdWithContainers() 목 데이터
        mockFindFeedByIdWithContainers();
        when(userService.findById(user.getId())).thenReturn(user);

        Container updateMenu = Container.of(feed, Menu.mainOf(restaurant, "updateMenu"), "test update");
        Container deleteMenu = Container.of(feed, Menu.mainOf(restaurant, "deleteMenu"), "test delete");
        feed.getContainerList().clear();
        feed.getContainerList().addAll(List.of(updateMenu, deleteMenu));

//        Restaurant newRestaurant = mock(Restaurant.class);
        when(restaurantService.findById(restaurant.getId())).thenReturn(restaurant);
        FeedMenuDto updateMenuDto = FeedMenuDto.builder()
                .menuName("updateMenu")
                .container("test update2")
                .build();

        FeedMenuDto createMenuDto = FeedMenuDto.builder()
                .menuName("createMenu")
                .container("test create")
                .build();

        FeedInfoDto dto = FeedInfoDto.builder()
                .category(Category.FAST_FOOD)
                .mainMenu(List.of(updateMenuDto, createMenuDto))
                .subMenu(List.of())
                .difficulty(3)
                .welcome(true)
                .thumbnailImageId(thumbnail.getId())
                .content("this is new Feed")
                .build();

        //when 함수를 콜하면
        feedService.updateFeed(feed.getId(), dto, user.getId());

        //then
        assertThat(feed.getCategory()).isEqualTo(dto.getCategory());
        assertThat(feed.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(feed.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(feed.getThumbnail().getId()).isEqualTo(dto.getThumbnailImageId());
        assertThat(feed.getContent()).isEqualTo(dto.getContent());

        assertThat(feed.getContainerList())
                .hasSize(2)
                .anyMatch(container -> equalsMenuAndDto(container, updateMenuDto))
                .anyMatch(container -> equalsMenuAndDto(container, createMenuDto));

        verify(restaurant).deleteFeedStatics(feed);
        verify(restaurant).updateFeedStatics(feed);
        verify(recommendFeedService).checkAndUpdate(feed);
    }

    @Test
    @DisplayName("피드 수정 테스트 - 식당 변경")
    void updateFeedRestaurantChange() {
        //given findByIdWithContainers() 목 데이터
        mockFindFeedByIdWithContainers();
        when(userService.findById(user.getId())).thenReturn(user);

        Container updateMenu = Container.of(feed, Menu.mainOf(restaurant, "updateMenu"), "test update");
        Container deleteMenu = Container.of(feed, Menu.mainOf(restaurant, "deleteMenu"), "test delete");
        feed.getContainerList().clear();
        feed.getContainerList().addAll(List.of(updateMenu, deleteMenu));

        Restaurant newRestaurant = mock(Restaurant.class);
        when(restaurantService.findById(newRestaurant.getId())).thenReturn(newRestaurant);
        FeedMenuDto updateMenuDto = FeedMenuDto.builder()
                .menuName("updateMenu")
                .container("test update2")
                .build();

        FeedMenuDto createMenuDto = FeedMenuDto.builder()
                .menuName("createMenu")
                .container("test create")
                .build();

        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantCreateDto(RestaurantInfoDto.from(newRestaurant))
                .category(Category.FAST_FOOD)
                .mainMenu(List.of(updateMenuDto, createMenuDto))
                .subMenu(List.of())
                .difficulty(3)
                .welcome(true)
                .thumbnailImageId(thumbnail.getId())
                .content("this is new Feed")
                .build();
        when(restaurantService.findByDto(dto.getRestaurantCreateDto())).thenReturn(newRestaurant);

        //when 함수를 콜하면
        feedService.updateFeed(feed.getId(), dto, user.getId());

        //then
        assertThat(feed.getRestaurant().getName()).isEqualTo(dto.getRestaurantCreateDto().getName());
        assertThat(feed.getCategory()).isEqualTo(dto.getCategory());
        assertThat(feed.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(feed.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(feed.getThumbnail().getId()).isEqualTo(dto.getThumbnailImageId());
        assertThat(feed.getContent()).isEqualTo(dto.getContent());

        assertThat(feed.getContainerList())
                .hasSize(2)
                .anyMatch(container -> equalsMenuAndDto(container, updateMenuDto))
                .anyMatch(container -> equalsMenuAndDto(container, createMenuDto));

        verify(newRestaurant).updateFeedStatics(feed);
        verify(restaurant).deleteFeedStatics(feed);
    }

    void mockFindFeedByIdWithContainers() {
        mainMenus = new ArrayList<>();
        for (int i = 1; i <= MENU_NUM; i++) {
            mainMenus.add(spy(Container.of(feed,
                    spy(Menu.mainOf(restaurant, "main" + i)), "mainCont" + i)));
        }
        subMenus = new ArrayList<>();
        for (int i = 1; i <= MENU_NUM; i++) {
            subMenus.add(spy(Container.of(feed,
                    spy(Menu.subOf(restaurant, "sub" + i)), "subCont" + i)));
        }
        feed.getContainerList().addAll(mainMenus);
        feed.getContainerList().addAll(subMenus);
        feed.setDifficulty(3);

        when(user.getId()).thenReturn(1L);
        when(feed.getId()).thenReturn(2L);
        when(restaurant.getId()).thenReturn(3L);
        when(feedRepository.findById(feed.getId())).thenReturn(of(feed));
        when(imageService.findById(any())).thenReturn(thumbnail);
    }

    private void assertEqualsMenuAndDto(List<Container> containerList, List<FeedMenuDto> dtoList) {
        List<Container> checker = new ArrayList<>(containerList);
        assertThat(dtoList.size()).isEqualTo(containerList.size());
        assertThat(dtoList)
                .allMatch(menuDto -> {
                    Iterator<Container> iter = checker.iterator();
                    while (iter.hasNext()) {
                        if (equalsMenuAndDto(iter.next(), menuDto)) {
                            iter.remove();
                            return true;
                        }
                    }
                    return false;
                });
    }

    private boolean equalsMenuAndDto(Container container, FeedMenuDto menuDto) {
        return menuDto.getMenuName().equals(container.getMenu().getName())
                && menuDto.getContainer().equals(container.getDescription());
    }
}