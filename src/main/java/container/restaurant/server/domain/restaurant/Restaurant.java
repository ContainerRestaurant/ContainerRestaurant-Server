package container.restaurant.server.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "TB_RESTAURANT")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotNull
    private String Name;

    @NotNull
    private String Address;

    @JsonIgnore
    @Column(nullable = false)
    private Point Location;

    @NotNull
    private float Latitude;

    @NotNull
    private float Longitude;

    @NotNull
    private Long image_ID;

    @ColumnDefault("0")
    private int VinishCount;

    @Builder
    public Restaurant(String name, String addr, Point loc, float lon, float lat) {
        setName(name);
        setAddress(addr);
        setLocation(loc);
        setLongitude(lon);
        setLatitude(lat);
        setImage_ID((long) 0);
    }

}
