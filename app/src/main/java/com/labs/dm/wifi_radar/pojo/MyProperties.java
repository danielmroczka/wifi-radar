package com.labs.dm.wifi_radar.pojo;

import android.content.SharedPreferences;

/**
 * Created by daniel on 2015-07-08.
 */
public class MyProperties {

    private static final String SCAN_INTERVAL = "scan.interval";
    private static final String GPS_MIN_TIME = "gps.minTime";
    private static final String GPS_MIN_DIST = "gps.minDist";
    private static final String NET_MIN_TIME = "net.minTime";
    private static final String NET_MIN_DIST = "net.minDist";

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

    private void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public int getGpsMinTime() {
        return gpsMinTime;
    }

    private void setGpsMinTime(int gpsMinTime) {
        this.gpsMinTime = gpsMinTime;
    }

    public float getGpsMinDist() {
        return gpsMinDist;
    }

    private void setGpsMinDist(float gpsMinDist) {
        this.gpsMinDist = gpsMinDist;
    }

    public int getNetMinTime() {
        return netMinTime;
    }

    private void setNetMinTime(int netMinTime) {
        this.netMinTime = netMinTime;
    }

    public float getNetMinDist() {
        return netMinDist;
    }

    private void setNetMinDist(float netMinDist) {
        this.netMinDist = netMinDist;
    }
}
