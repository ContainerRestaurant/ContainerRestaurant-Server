package container.restaurant.server.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import container.restaurant.server.domain.base.BaseTimeEntity;
import container.restaurant.server.domain.feed.Container;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.restaurant.menu.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static container.restaurant.server.utils.SpatialUtils.createPointType;

@Getter
@NoArgsConstructor
@Entity(name = "TB_RESTAURANT")
public class Restaurant extends BaseTimeEntity {

    @NotNull
    private String name;

    @NotNull
    private String address;

    @JsonIgnore
    private Point location;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    private List<Menu> menu;

    @OneToOne(fetch = FetchType.LAZY)
    private Image thumbnail;

    @ColumnDefault("0")
    private int vanishCount;

    @ColumnDefault("0")
    private int favoriteCount;

    @ColumnDefault("0")
    private int feedCount;

    @ColumnDefault("0.0")
    private float difficultySum;

    @ColumnDefault("0")
    private int welcomeCount;

    private String bestMenu1;

    private String bestMenu2;

    @SneakyThrows
    @Builder
    protected Restaurant(String name, String addr, double lon, double lat, Image thumbnail) {
        this.name = name;
        this.address = addr;
        this.location = createPointType(lat, lon);
        this.longitude = lon;
        this.latitude = lat;
        this.thumbnail = thumbnail;
    }

    public void favoriteCountUp() {
        this.favoriteCount++;
    }

    public void favoriteCountDown() {
        this.favoriteCount--;
    }

    public void VanishCountUp() {
        this.vanishCount++;
    }

    public float getDifficultyAvg() {
        if (this.difficultySum == 0)
            return 0.0f;
        return this.difficultySum / this.feedCount;
    }

    public boolean isContainerFriendly() {
        return  welcomeCount >= 2;
    }

    public void updateFeedStatics(Feed feed) {
        this.feedCount++;
        this.difficultySum += feed.getDifficulty();
        if (feed.getWelcome()) welcomeCount++;
        updateThumbnailIfNull(feed.getThumbnail());
        updateMenusStatics(getMenuList(feed));
    }

    private void updateThumbnailIfNull(Image thumbnail) {
        if (this.thumbnail == null && thumbnail != null) {
            this.thumbnail = thumbnail;
        }
    }

    private void updateMenusStatics(List<Menu> menus) {
        menus.forEach(menu -> {
            menu.countUp();
            if (bestMenu1 == null) {
                bestMenu1 = menu.getName();
            } else if (bestMenu2 == null) {
                bestMenu2 = menu.getName();
            }
        });
    }

    public void deleteFeedStatics(Feed feed) {
        this.feedCount--;
        this.difficultySum -= feed.getDifficulty();
        if (feed.getWelcome()) welcomeCount--;
        deleteThumbnailIfSame(feed.getThumbnail());
        deleteMenusStatics(getMenuList(feed));
    }

    private void deleteThumbnailIfSame(Image thumbnail) {
        if (Objects.equals(this.thumbnail, thumbnail)) {
            this.thumbnail = null;
        }
    }

    private void deleteMenusStatics(List<Menu> menus) {
        menus.forEach(Menu::countDown);
    }

    private List<Menu> getMenuList(Feed feed) {
        return feed.getContainerList().stream()
                .map(Container::getMenu)
                .collect(Collectors.toList());
    }

    public void setBestMenu(List<Menu> bestMenu) {
        this.bestMenu1 = this.bestMenu2 = null;
        if (!bestMenu.isEmpty()) this.bestMenu1 = bestMenu.get(0).getName();
        if (bestMenu.size() > 1) this.bestMenu2 = bestMenu.get(1).getName();
    }

    public List<String> getBestMenu() {
        if (bestMenu2 != null) {
            return List.of(bestMenu1, bestMenu2);
        } else if (bestMenu1 != null) {
            return List.of(bestMenu1);
        } else return List.of();
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
}
