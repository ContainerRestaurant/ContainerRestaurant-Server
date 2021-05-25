package container.restaurant.server.domain.feed.container;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.restaurant.menu.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContainerServiceTest extends BaseServiceTest {

    @InjectMocks
    ContainerService containerService;

    @Mock
    Feed feed;

    @Test
    @DisplayName("용기 및 메뉴 저장 테스트")
    void saveTest() {
        //given 저장할 container, menu 와 persist 된 menu, save 된 container 가 있을 때
        Menu orgMenu1 = mock(Menu.class);
        Menu orgMenu2 = mock(Menu.class);
        List<Container> containerList = List.of(
                spy(Container.of(feed, orgMenu1, "contain1")),
                spy(Container.of(feed, orgMenu2, "contain2"))
        );
        Menu menu1 = mock(Menu.class);
        Menu menu2 = mock(Menu.class);
        when(menuService.save(any())).thenReturn(menu1, menu2);
        Container container1 = Container.of(feed, null, "contain3");
        Container container2 = Container.of(feed, null, "contain4");
        when(containerRepository.save(any())).thenReturn(container1, container2);

        //when 함수를 콜 했을 때
        containerService.save(containerList);

        //then-1 각각의 menu 가 persist, container 는 save 된다.
        verify(containerRepository).save(containerList.get(0));
        verify(containerRepository).save(containerList.get(1));
        verify(containerList.get(0)).setMenu(menu1);
        verify(containerList.get(1)).setMenu(menu2);
        verify(menuService).save(orgMenu1);
        verify(menuService).save(orgMenu2);
        verify(menuService).save(orgMenu1);
        verify(menuService).save(orgMenu2);
    }
}