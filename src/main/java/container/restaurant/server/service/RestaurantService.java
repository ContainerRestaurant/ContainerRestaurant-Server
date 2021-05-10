package container.restaurant.server.service;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.web.dto.restaurant.RestaurantNearInfoDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ImageRepository imageRepository;

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(Long id) throws NotFoundException {

        return restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{" + id + "}에 해당하는 식당이 없습니다."));
    }

    public List<RestaurantNearInfoDto> findNearByRestaurants(double lat, double lon, long radius) {
        return restaurantRepository.findNearByRestaurants(lat, lon, radius)
                .stream()
                .map(RestaurantNearInfoDto::from)
                .collect(Collectors.toList());
    }
}
