package container.restaurant.server.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import container.restaurant.server.domain.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
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

    @NotNull
    private Long image_ID;

    @ColumnDefault("0")
    private int vanishCount;

    @ColumnDefault("0")
    private int favoriteCount;

    @ColumnDefault("0")
    private int feedCount;

    @ColumnDefault("0.0")
    private float difficultySum;

    @SneakyThrows
    @Builder
    protected Restaurant(String name, String addr, double lon, double lat, Long image_ID) {
        this.name = name;
        this.address = addr;
        this.location = createPointType(lat, lon);
        this.longitude = lon;
        this.latitude = lat;
        this.image_ID = image_ID;
    }

    public void favoriteCountUp() {
        this.favoriteCount++;
    }

    public void favoriteCountDown() {
        this.favoriteCount--;
    }

    public void feedCountUp() {
        this.feedCount++;
    }

    public void feedCountDown() {
        this.feedCount--;
    }

    public void VanishCountUp() {
        this.vanishCount++;
    }

    public void addDifficultySum(int difficulty) {
        this.difficultySum += difficulty;
    }

    public void subDifficultySum(int difficulty) {
        this.difficultySum -= difficulty;
    }

    public float getDifficultyAvg() {
        if (this.difficultySum == 0)
            return 0.0f;
        return this.difficultySum / this.feedCount;
    }
}
