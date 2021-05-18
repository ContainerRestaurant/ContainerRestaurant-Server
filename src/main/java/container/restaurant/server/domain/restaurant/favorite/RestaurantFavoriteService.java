package container.restaurant.server.domain.restaurant.favorite;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.restaurant.favorite.RestaurantFavoriteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantFavoriteService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    private final ImageRepository imageRepository;

    private final UserService userService;

    @Transactional
    public void userFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = userService.findById(userId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 식당입니다.(id:" + restaurantId + ")"));

        restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant)
                .ifPresentOrElse(
                        restaurantFavorite -> {/* do nothing*/},
                        () -> restaurantFavoriteRepository.save(RestaurantFavorite.of(user, restaurant))
                );
    }

    @Transactional
    public void userCancelFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = userService.findById(userId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 식당입니다.(id:" + restaurantId + ")"));

        restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant)
                .ifPresent(restaurantFavoriteRepository::delete);
    }

    @Transactional
    public List<RestaurantFavoriteDto> userFindAllFavoriteRestaurant(Long userId) {
        User user = userService.findById(userId);

        return restaurantFavoriteRepository.findAllByUser(user)
                .stream()
                .map(restaurantFavorite -> {
                    Image image = imageRepository.getOne(restaurantFavorite.getRestaurant().getImage_ID());
                    return RestaurantFavoriteDto.from(restaurantFavorite, image);
                })
                .collect(Collectors.toList());
    }
}
