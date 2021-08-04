package container.restaurant.server.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.picture.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import static container.restaurant.server.utils.SpatialUtils.createPointType;

@Getter
@NoArgsConstructor
@Entity(name = "TB_RESTAURANT")
public class Restaurant extends BaseEntity {

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

    public void feedCountUp(Feed feed) {
        this.feedCount++;
        this.difficultySum += feed.getDifficulty();
        if (feed.getWelcome()) welcomeCount++;
        feed.getContainerList().forEach(container ->
                container.getMenu().countUp());
    }

    public void feedCountDown(Feed feed) {
        this.feedCount--;
        this.difficultySum -= feed.getDifficulty();
        if (feed.getWelcome()) welcomeCount--;
        feed.getContainerList().forEach(container ->
                container.getMenu().countDown());
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

    public void setThumbnail(Image thumbnail){
        this.thumbnail =  thumbnail;
    }
}
