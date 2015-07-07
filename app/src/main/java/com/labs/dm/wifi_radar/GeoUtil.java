package com.labs.dm.wifi_radar;

/**
 * Created by daniel on 2015-07-07.
 */
public class GeoUtil {
    public static final int MEAN_EARTH_RADIUS = 6371000;

    /**
     * @param src
     * @param dest
     * @return distance in meters
     */
    public static double calculateDistance(Position src, Position dest) {

        double latDistance = Math.toRadians(src.getLatitude() - dest.getLatitude());
        double lngDistance = Math.toRadians(src.getLongitude() - dest.getLongitude());

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2))
                + (Math.cos(Math.toRadians(src.getLatitude())))
                * (Math.cos(Math.toRadians(dest.getLatitude())))
                * (Math.sin(lngDistance / 2))
                * (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return MEAN_EARTH_RADIUS * c;
    }

}
