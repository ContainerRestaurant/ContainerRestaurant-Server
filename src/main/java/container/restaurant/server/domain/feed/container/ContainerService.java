package container.restaurant.server.domain.feed.container;

import container.restaurant.server.domain.restaurant.menu.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContainerService {

    private final ContainerRepository containerRepository;

    private final MenuService menuService;

    @Transactional
    public Collection<Container> save(Collection<Container> containerList) {
        return containerList.stream()
                .map(container -> {
                    container.setMenu(menuService.save(container.getMenu()));
                    return containerRepository.save(container);
                })
                .collect(Collectors.toList());
    }
}
