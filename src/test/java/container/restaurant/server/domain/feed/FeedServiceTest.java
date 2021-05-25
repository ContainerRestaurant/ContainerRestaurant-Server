package container.restaurant.server.domain.feed;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.menu.Menu;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.web.dto.feed.FeedDetailDto;
import container.restaurant.server.web.dto.feed.FeedInfoDto;
import container.restaurant.server.web.dto.feed.FeedMenuDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedServiceTest extends BaseServiceTest {

    private static final int MENU_NUM = 3;

    @Spy
    @InjectMocks
    private Feed feed;

    @Mock
    private User user;
    @Mock
    private Restaurant restaurant;

    private List<Container> mainMenus;
    private List<Container> subMenus;

    @InjectMocks
    private FeedService feedService;

    @Captor
    ArgumentCaptor<Feed> feedCaptor;
    @Captor
    ArgumentCaptor<Collection<Container>> containersCaptor;

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
        when(restaurantService.findById(restaurant.getId())).thenReturn(restaurant);

        FeedMenuDto mainMenuDto = FeedMenuDto.builder()
                .menuName("mainMenu")
                .container("mainMenuContainer")
                .build();

        FeedMenuDto subMenuDto = FeedMenuDto.builder()
                .menuName("subMenu")
                .container("subMenuContainer")
                .build();

        FeedInfoDto dto = FeedInfoDto.builder()
                .restaurantId(restaurant.getId())
                .category(Category.FAST_FOOD)
                .mainMenu(List.of(mainMenuDto))
                .subMenu(List.of(subMenuDto))
                .difficulty(3)
                .welcome(true)
                .thumbnailUrl("test url")
                .content("this is new Feed")
                .build();

        //when 함수를 콜하면
        feedService.createFeed(dto, user.getId());

        //then FeedInfoDto 정보와 동등한 Feed, FeedMenuDto 정보와 동등한 Containers 가 save 된다.
        verify(feedRepository).save(feedCaptor.capture());
        Feed saved = feedCaptor.getValue();
        assertThat(saved.getOwner()).isEqualTo(user);
        assertThat(saved.getRestaurant()).isEqualTo(restaurant);
        assertThat(saved.getCategory()).isEqualTo(dto.getCategory());
        assertThat(saved.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(saved.getWelcome()).isEqualTo(dto.getWelcome());
        assertThat(saved.getThumbnailUrl()).isEqualTo(dto.getThumbnailUrl());
        assertThat(saved.getContent()).isEqualTo(dto.getContent());

        verify(containerService).save(containersCaptor.capture());
        assertThat(containersCaptor.getValue())
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
        InOrder order = inOrder(containerService, feedHitRepository, feedRepository);
        order.verify(containerService).deleteAll(feed.getContainerList());
        order.verify(feedHitRepository).deleteAllByFeed(feed);
        order.verify(feedRepository).delete(feed);
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