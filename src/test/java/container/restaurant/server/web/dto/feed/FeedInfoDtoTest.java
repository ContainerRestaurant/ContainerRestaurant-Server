package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.container.Container;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedInfoDtoTest {

    @Test
    @DisplayName("Feed 변환 테스트")
    void toFeedWith() {
        //given User, Restaurant, FeedInfoDto 가 주어졌을 때
        long userId, restaurantId;

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId = 3L);

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(restaurantId = 5L);

        FeedInfoDto feedInfoDto = FeedInfoDto.builder()
                .restaurantId(restaurantId)
                .category(Category.NIGHT_MEAL)
                .difficulty(4)
                .thumbnailUrl("test/url")
                .content("test content")
                .welcome(true)
                .build();

        //when User, Restaurant 를 이용해 dto 를 Feed 로 변환하면
        Feed feed = feedInfoDto.toFeedWith(user, restaurant);

        //then User, Restaurant 와 dto 속성들이 잘 세팅되어있다.
        assertThat(feed.getOwner().getId()).isEqualTo(userId);
        assertThat(feed.getRestaurant().getId()).isEqualTo(restaurantId);
        assertThat(feed.getCategory()).isEqualTo(feedInfoDto.getCategory());
        assertThat(feed.getDifficulty()).isEqualTo(feedInfoDto.getDifficulty());
        assertThat(feed.getThumbnailUrl()).isEqualTo(feedInfoDto.getThumbnailUrl());
        assertThat(feed.getContent()).isEqualTo(feedInfoDto.getContent());
        assertThat(feed.getWelcome()).isEqualTo(feedInfoDto.getWelcome());

    }

    @Test
    @DisplayName("Containers 변환 테스트")
    void toContainersWith() {
        //given feed, restaurant 와 mainMenu, subMenu 가 포함된 FeedInfoDto 가 주어졌을 때
        long feedId, restaurantId;

        Feed feed = mock(Feed.class);
        when(feed.getId()).thenReturn(feedId = 4L);

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(restaurantId = 6L);

        FeedMenuDto mainMenu = new FeedMenuDto("main menu", "this is main menu");
        FeedMenuDto subMenu = new FeedMenuDto("sub menu", "this is sub menu");

        FeedInfoDto feedInfoDto = FeedInfoDto.builder()
                .mainMenu(List.of(mainMenu))
                .subMenu(List.of(subMenu))
                .build();

        //when Feed, Restaurant 를 이용해 dto 를 Containers 로 변환하면
        List<Container> containers = feedInfoDto.toContainerListWith(feed, restaurant);

        //then mainMenu, subMenu 에 대한 Container 가 있고, feed, restaurant 와 관련되어있다
        assertThat(containers).hasSize(2)
                .anySatisfy(container -> {
                    assertThat(container.getDescription()).isEqualTo(mainMenu.getContainer());
                    assertThat(container.getMenu().getName()).isEqualTo(mainMenu.getMenuName());
                    assertThat(container.getFeed().getId()).isEqualTo(feedId);
                    assertThat(container.getMenu().getRestaurant().getId()).isEqualTo(restaurantId);
                    assertThat(container.getMenu().getIsMain()).isTrue();
                })
                .anySatisfy(container -> {
                    assertThat(container.getDescription()).isEqualTo(subMenu.getContainer());
                    assertThat(container.getMenu().getName()).isEqualTo(subMenu.getMenuName());
                    assertThat(container.getFeed().getId()).isEqualTo(feedId);
                    assertThat(container.getMenu().getRestaurant().getId()).isEqualTo(restaurantId);
                    assertThat(container.getMenu().getIsMain()).isFalse();
                });
    }
}