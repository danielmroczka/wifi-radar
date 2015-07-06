package com.labs.dm.wifi_radar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by daniel on 2015-07-06.
 */
public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context) {
        super(context, "wifi.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table network(ssid TEXT, power INTEGER, lang DOUB, lat DOUB, timestamp NUMERIC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void add(String ssid, int power, double lang, double lat, long timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("ssid", ssid);
        content.put("power", power);
        content.put("lang", lang);
        content.put("lat", lat);
        content.put("timestamp", timestamp);

        db.insertOrThrow("network", null, content);
    }

}
