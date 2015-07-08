package com.labs.dm.wifi_radar.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.labs.dm.wifi_radar.pojo.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by daniel on 2015-07-06.
 */
public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context) {
        super(context, "wifi3.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table network(id INTEGER PRIMARY KEY, ssid TEXT, bssid TEXT UNIQUE, channel INTEGER, type TEXT, added DATETIME CURRENT_TIMESTAMP )");
        db.execSQL("create table signal(id INTEGER PRIMARY KEY, id_network INTEGER, level INTEGER, longitude DOUB, latitude DOUB, accuracy NUMERIC, timestamp NUMERIC, FOREIGN KEY(id_network) REFERENCES network(id))");
        db.execSQL("create unique index network_bssid on network(bssid)");
        db.execSQL("create view VNETWORKS as select n.ssid, max(s.level) from network n, signal s where n.id = s.id_network group by n.ssid");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Add item to network table.
     *
     * @param ssid
     * @param bssid
     * @return rowid new created item or -1 if item already exist
     */
    public long addNetwork(String ssid, String bssid, int channel, String type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("ssid", ssid);
        content.put("bssid", bssid);
        content.put("channel", channel);
        content.put("type", type);
        content.put("added", new Date().toString());
        long rowId;

        try {
            rowId = db.insertOrThrow("network", null, content);
        } finally {
            db.close();
        }

        return rowId;
    }

    /**
     * Returns Id_Network for bssid.
     *
     * @param bssid
     * @return RowId or -1
     */
    public long getIdNetwork(String bssid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        long id = -1;
        try {
            c = db.rawQuery("SELECT id FROM network where bssid='" + bssid + "'", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                id = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();

        }

        return id;
    }

    public long addSignal(long id_network, int level, double longitude, double latitude, float accuracy, long timestamp) {
        System.out.println("Add Signal " + id_network);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("id_network", id_network);
        content.put("level", level);
        content.put("longitude", longitude);
        content.put("latitude", latitude);
        content.put("accuracy", accuracy);
        content.put("timestamp", timestamp);
        long res = db.insertOrThrow("signal", null, content);
        db.close();
        return res;
    }

    public List<Position> getPositions(long id_network) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT longitude, latitude FROM SIGNAL where id_network=" + id_network, null);
        c.moveToFirst();
        List<Position> list = new ArrayList<>(c.getCount());
        if (c.getCount() > 0) {
            while (!c.isLast()) {
                Position p = new Position(c.getDouble(0), c.getDouble(1));
                list.add(p);
                c.move(1);
            }
        }

        c.close();
        db.close();

        return list;

    }

}
