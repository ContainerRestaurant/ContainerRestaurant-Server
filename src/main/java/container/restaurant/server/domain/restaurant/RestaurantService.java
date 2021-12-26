package container.restaurant.server.domain.restaurant;

import container.restaurant.server.domain.restaurant.dto.RestaurantThumbnailDto;
import container.restaurant.server.domain.restaurant.favorite.RestaurantFavoriteRepository;
import container.restaurant.server.domain.restaurant.menu.Menu;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.restaurant.RestaurantDetailDto;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import container.restaurant.server.web.dto.restaurant.RestaurantNearInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    @Transactional(readOnly = true)
    public RestaurantDetailDto getRestaurantInfoById(Long restaurantId, Long loginId) {
        Restaurant restaurant = findById(restaurantId);
        return RestaurantDetailDto.from(restaurant,
                restaurantFavoriteRepository.existsByUserIdAndRestaurantId(loginId, restaurantId));
    }

    @Transactional(readOnly = true)
    public List<RestaurantNearInfoDto> findNearByRestaurants(double lat, double lon, long radius, Long loginId) {
        return restaurantRepository.findNearByRestaurants(lat, lon, radius)
                .stream()
                .map(restaurant -> RestaurantNearInfoDto.from(restaurant,
                        restaurantFavoriteRepository.existsByUserIdAndRestaurantId(loginId, restaurant.getId())))
                .collect(Collectors.toList());
    }

    // 식당 이름 검색 비활성화

    @Transactional
    public void restaurantVanish(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.VanishCountUp();
    }

    @Transactional(readOnly = true)
    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 식당입니다.(id:" + id + ")"));
    }

    @Transactional
    public Restaurant findByDto(RestaurantInfoDto dto) {
        return restaurantRepository.findByName(dto.getName())
                .orElseGet(() -> restaurantRepository.save(dto.toEntity()));
    }

    @Transactional
    public Pageable updateBestMenusPage(Pageable pageable) {
        LocalDateTime fromDate = LocalDate.now().minusDays(7).atStartOfDay();
        var restaurants = restaurantRepository.selectForBestMenuUpdate(fromDate, pageable);
        for (Restaurant restaurant : restaurants) {
            List<Menu> bestMenu = restaurant.getMenu().stream()
                    .sorted(Comparator.comparingInt(menu -> -menu.getCount()))
                    .limit(2)
                    .collect(Collectors.toList());
            restaurant.setBestMenu(bestMenu);
        }
        return restaurants.nextPageable();
    }

    @Transactional
    public Pageable updateThumbnailPage(Pageable pageable) {
        LocalDateTime fromDate = LocalDate.now().minusMonths(1).atStartOfDay();
        var dtoPage = restaurantRepository.selectForRestaurantThumbnailUpdate(fromDate, pageable);

        for (RestaurantThumbnailDto dto : dtoPage) {
            dto.getRestaurant().setThumbnail(dto.getFeedThumbnail());
        }

        return dtoPage.nextPageable();
    }

    @Transactional
    public void delete(Restaurant restaurant) {
        restaurantRepository.delete(restaurant);
    }
}