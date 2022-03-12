package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.restaurant.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MenuServiceTest extends BaseServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    Restaurant restaurant;

    @Test
    @DisplayName("메뉴 영속 테스트 - 생성")
    void persistCreateTest() {
        //given DB 에 매핑되지 않는 비영속 menu 가 주어졌을 때
        Menu menu = Menu.mainOf(restaurant, "menu");
        Menu expect = mock(Menu.class);
        when(menuRepository.findByRestaurantAndName(restaurant, "menu"))
                .thenReturn(Optional.empty());
        when(menuRepository.save(menu))
                .thenReturn(expect);

        //when 해당 메뉴를 persist 하면
        Menu actual = menuService.save(menu);

        //then menu 가 저장되고 countUp 된다.
        assertThat(actual).isEqualTo(expect);
        verify(expect).countUp();
        verify(menuRepository).save(menu);
    }

    @Test
    @DisplayName("메뉴 영속 테스트 - 수정")
    void persistModifyTest() {
        //given DB 에 매핑 된 비영속 menu 가 주어졌을 때
        Menu menu = Menu.mainOf(restaurant, "menu");
        Menu expect = mock(Menu.class);
        when(menuRepository.findByRestaurantAndName(restaurant, "menu"))
                .thenReturn(of(expect));

        //when 해당 메뉴를 persist 하면
        Menu actual = menuService.save(menu);

        //then menu 가 저장되지 않고 countUp 된다.
        assertThat(actual).isEqualTo(expect);
        verify(expect).countUp();
        verify(menuRepository, never()).save(menu);
    }

    @Test
    @DisplayName("메뉴 삭제 테스트 - 수정")
    void deleteModifyTest() {
        //given DB 에 countDown 된 결과 3인 데이터의 비영속 menu 가 주어졌을 때
        Menu menu = Menu.mainOf(restaurant, "menu");
        Menu expect = mock(Menu.class);
        when(menuRepository.findByRestaurantAndName(restaurant, "menu"))
                .thenReturn(of(expect));
        when(expect.countDown()).thenReturn(3);

        //when 해당 메뉴를 delete 하면
        menuService.delete(menu);

        //then menu 가 countDown 되고 삭제되지 않는다.
        verify(expect).countDown();
        verify(menuRepository, never()).delete(expect);
    }

    @Test
    @DisplayName("메뉴 삭제 테스트 - 삭제")
    void deleteTest() {
        //given DB 에 countDown 된 결과 0인 데이터의 비영속 menu 가 주어졌을 때
        Menu menu = Menu.mainOf(restaurant, "menu");
        Menu expect = mock(Menu.class);
        when(menuRepository.findByRestaurantAndName(restaurant, "menu"))
                .thenReturn(of(expect));
        when(expect.countDown()).thenReturn(0);

        //when 해당 메뉴를 delete 하면
        menuService.delete(menu);

        //then menu 가 countDown 되고 삭제 된다.
        verify(expect).countDown();
        verify(menuRepository).delete(expect);
    }

}