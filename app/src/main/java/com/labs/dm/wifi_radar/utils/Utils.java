package com.labs.dm.wifi_radar.utils;

import com.labs.dm.wifi_radar.pojo.Position;

/**
 * Created by daniel on 2015-07-07.
 */
public class Utils {
    private static final int MEAN_EARTH_RADIUS = 6371000;

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

    /**
     * Translates network frequency into channel
     *
     * @param freq
     * @return channel
     */
    public static int toChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

}
