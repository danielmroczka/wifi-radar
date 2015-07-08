package com.labs.dm.wifi_radar.pojo;

/**
 * Created by daniel on 2015-07-07.
 */
public class Position {
    private double latitude;
    private double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
