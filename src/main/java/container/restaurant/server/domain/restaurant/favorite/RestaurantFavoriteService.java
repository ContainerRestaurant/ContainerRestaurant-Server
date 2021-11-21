package container.restaurant.server.domain.restaurant.favorite;

import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantService;
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

    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;

    @Transactional
    public void userFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = userService.findById(userId);
        Restaurant restaurant = restaurantService.findById(restaurantId);

        restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant)
                .ifPresentOrElse(
                        restaurantFavorite -> {/* do nothing*/},
                        () -> {
                            restaurantFavoriteRepository.save(RestaurantFavorite.of(user, restaurant));
                            user.bookmarkedCountUp();
                            restaurant.favoriteCountUp();
                        }
                );
    }

    @Transactional
    public void userCancelFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = userService.findById(userId);
        Restaurant restaurant = restaurantService.findById(restaurantId);

        restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant)
                .ifPresent(restaurantFavorite -> {
                    restaurantFavoriteRepository.delete(restaurantFavorite);
                    user.bookmarkedCountDown();
                    restaurant.favoriteCountDown();
                });
    }

    @Transactional(readOnly = true)
    public List<RestaurantFavoriteDto> findAllByUserId(Long userId) {
        User user = userService.findById(userId);

        return restaurantFavoriteRepository.findAllByUser(user)
                .stream()
                .map(RestaurantFavoriteDto::from)
                .collect(Collectors.toList());
    }
}
