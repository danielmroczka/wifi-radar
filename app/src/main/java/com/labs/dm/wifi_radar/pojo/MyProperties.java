package com.labs.dm.wifi_radar.pojo;

import android.content.SharedPreferences;

/**
 * Created by daniel on 2015-07-08.
 */
public class MyProperties {

    public static final String SCAN_INTERVAL = "scan.interval";
    public static final String GPS_MIN_TIME = "gps.minTime";
    public static final String GPS_MIN_DIST = "gps.minDist";
    public static final String NET_MIN_TIME = "net.minTime";
    public static final String NET_MIN_DIST = "net.minDist";

    private int scanInterval;
    private int gpsMinTime;
    private float gpsMinDist;
    private int netMinTime;
    private float netMinDist;

    public void load(SharedPreferences sp) {
        setScanInterval(1000 * Integer.parseInt(sp.getString(MyProperties.SCAN_INTERVAL, "5")));
        setGpsMinTime(1000 * Integer.parseInt(sp.getString(MyProperties.GPS_MIN_TIME, "1")));
        setGpsMinDist(Float.parseFloat(sp.getString(MyProperties.GPS_MIN_DIST, "1")));
        setNetMinTime(1000 * Integer.parseInt(sp.getString(MyProperties.NET_MIN_TIME, "30")));
        setNetMinDist(Float.parseFloat(sp.getString(MyProperties.NET_MIN_DIST, "10")));
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public int getGpsMinTime() {
        return gpsMinTime;
    }

    public void setGpsMinTime(int gpsMinTime) {
        this.gpsMinTime = gpsMinTime;
    }

    public float getGpsMinDist() {
        return gpsMinDist;
    }

    public void setGpsMinDist(float gpsMinDist) {
        this.gpsMinDist = gpsMinDist;
    }

    public int getNetMinTime() {
        return netMinTime;
    }

    public void setNetMinTime(int netMinTime) {
        this.netMinTime = netMinTime;
    }

    public float getNetMinDist() {
        return netMinDist;
    }

    public void setNetMinDist(float netMinDist) {
        this.netMinDist = netMinDist;
    }
}
