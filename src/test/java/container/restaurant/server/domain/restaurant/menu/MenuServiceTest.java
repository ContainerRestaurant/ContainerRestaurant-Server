package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.BaseServiceTest;
import container.restaurant.server.domain.restaurant.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

}