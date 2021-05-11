package container.restaurant.server.domain.restaurant;

import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.web.dto.restaurant.RestaurantInfoDto;
import container.restaurant.server.web.dto.restaurant.RestaurantNameInfoDto;
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

    public RestaurantInfoDto findById(Long id) {
        return RestaurantInfoDto.from(restaurantRepository.findById(id).get());
    }

    public List<RestaurantNearInfoDto> findNearByRestaurants(double lat, double lon, long radius) {
        return restaurantRepository.findNearByRestaurants(lat, lon, radius)
                .stream()
                .map(RestaurantNearInfoDto::from)
                .collect(Collectors.toList());
    }

    public List<RestaurantNameInfoDto> searchRestaurantName(String name) {
        return restaurantRepository.searchRestaurantName(name)
                .stream()
                .map(RestaurantNameInfoDto::from)
                .collect(Collectors.toList());
    }
}
