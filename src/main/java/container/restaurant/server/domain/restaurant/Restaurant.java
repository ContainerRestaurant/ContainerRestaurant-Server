package container.restaurant.server.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import container.restaurant.server.domain.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
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

}
