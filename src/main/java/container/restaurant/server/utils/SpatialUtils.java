package container.restaurant.server.utils;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class SpatialUtils {
    public static Point createPointType(double lat, double lon) throws ParseException {
        String pointWKT = String.format("POINT(%s %s)", lon, lat);
        return (Point) new WKTReader().read(pointWKT);
    }
}
