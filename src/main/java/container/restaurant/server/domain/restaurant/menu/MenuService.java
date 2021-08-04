package container.restaurant.server.domain.restaurant.menu;

import container.restaurant.server.domain.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public Menu save(Menu menu) {
        Menu saved = menuRepository.findByRestaurantAndName(menu.getRestaurant(), menu.getName())
                .orElseGet(() -> menuRepository.save(menu));
        saved.countUp();
        return saved;
    }

    public void delete(Menu menu) {
        menuRepository.findByRestaurantAndName(menu.getRestaurant(), menu.getName())
                .ifPresent(m -> {
                    if (m.countDown() == 0)
                        menuRepository.delete(m);
                });
    }

    @Transactional(readOnly = true )
    public Set<String> findTop2ByRestaurantAndIsMainTrueMenuNameList(Restaurant restaurant){
        return menuRepository.findTop2ByRestaurantAndIsMainTrueAndNameNotOrderByCountDesc(restaurant,"")
                .stream().map(Menu::getName)
                .collect(Collectors.toSet());
    }
}
