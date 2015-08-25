package chau.streetparking.util;

import android.graphics.Point;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chau Thai on 8/25/15.
 */
public class MapUtil {
    private static final double EARTH_RADIUS = 6378100.0;

    /**
     * Convert meters to pixels on the map
     * @param map current map
     * @param latLng current camera position
     * @param distance distance in meters to be converted to pixels
     * @return Distance in pixels, or 0 if failed to convert
     */
    public static int convertMetersToPixels(GoogleMap map, LatLng latLng, double distance) {
        if (map == null || latLng == null)
            return 0;

        double lat1 = distance / EARTH_RADIUS;
        double lng1 = distance / (EARTH_RADIUS * Math.cos((Math.PI * latLng.latitude / 180)));

        double lat2 = latLng.latitude + lat1 * 180 / Math.PI;
        double lng2 = latLng.longitude + lng1 * 180 / Math.PI;

        Point p1 = map.getProjection().toScreenLocation(new LatLng(latLng.latitude, latLng.longitude));
        Point p2 = map.getProjection().toScreenLocation(new LatLng(lat2, lng2));

        return Math.abs(p1.x - p2.x);
    }

    /**
     *
     * @param latLng1
     * @param latLng2
     * @return distance in meters
     */
    public static float getDistance(LatLng latLng1, LatLng latLng2) {
        Location loc1 = new Location("");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(latLng1.latitude);
        loc2.setLongitude(latLng2.longitude);

        return loc1.distanceTo(loc2);
    }
}
