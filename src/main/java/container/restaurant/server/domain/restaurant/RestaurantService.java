package container.restaurant.server.domain.restaurant;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import container.restaurant.server.web.dto.restaurant.RestaurantNearInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public RestaurantInfoDto getRestaurantInfoById(Long id) {
        Restaurant restaurant = findById(id);
        Image image = imageService.findById(restaurant.getImage_ID());
        return RestaurantInfoDto.from(restaurant, image);
    }

    @Transactional(readOnly = true)
    public List<RestaurantNearInfoDto> findNearByRestaurants(double lat, double lon, long radius) {
        return restaurantRepository.findNearByRestaurants(lat, lon, radius)
                .stream()
                .map(restaurant -> {
                    Image image = imageService.findById(restaurant.getImage_ID());
                    return RestaurantNearInfoDto.from(restaurant, image);
                })
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
}