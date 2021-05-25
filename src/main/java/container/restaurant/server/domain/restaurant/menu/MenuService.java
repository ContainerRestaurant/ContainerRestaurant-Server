package container.restaurant.server.domain.restaurant.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
