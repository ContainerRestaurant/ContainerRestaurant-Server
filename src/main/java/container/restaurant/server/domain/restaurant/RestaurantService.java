package container.restaurant.server.domain.restaurant;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import container.restaurant.server.web.dto.restaurant.RestaurantNameInfoDto;
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

    public RestaurantInfoDto getRestaurantInfoById(Long id) {
        Restaurant restaurant = findById(id);
        Image image = imageService.findById(restaurant.getImage_ID());

        return RestaurantInfoDto.from(restaurant, image);
    }

    public List<RestaurantNearInfoDto> findNearByRestaurants(double lat, double lon, long radius) {
        return restaurantRepository.findNearByRestaurants(lat, lon, radius)
                .stream()
                .map(restaurant -> {
                    Image image = imageService.findById(restaurant.getImage_ID());
                    return RestaurantNearInfoDto.from(restaurant, image);
                })
                .collect(Collectors.toList());
    }

    public List<RestaurantNameInfoDto> searchRestaurantName(String name) {
        return restaurantRepository.searchRestaurantName(name)
                .stream()
                .map(RestaurantNameInfoDto::from)
                .collect(Collectors.toList());
    }

    public void updateVanish(Long id) {
        restaurantRepository.updateVanish(id);
    }

    @Transactional(readOnly = true)
    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "존재하지 않는 식당입니다.(id:" + id + ")"));
    }
}