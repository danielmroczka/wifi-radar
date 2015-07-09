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

    private final SQLiteDatabase writableDatabase;
    private final SQLiteDatabase readableDatabase;
    public final static String DB_NAME = "wifi3.db";

    public DBManager(Context context, String name) {
        super(context, name, null, 1);
        writableDatabase = getWritableDatabase();
        readableDatabase = getReadableDatabase();
    }

    public DBManager(Context context) {
        this(context, DB_NAME);
    }


    @Override
    public synchronized void close() {
        writableDatabase.close();
        readableDatabase.close();
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
        ContentValues content = new ContentValues();
        content.put("ssid", ssid);
        content.put("bssid", bssid);
        content.put("channel", channel);
        content.put("type", type);
        content.put("added", new Date().toString());
        long rowId;

        try {
            rowId = writableDatabase.insertOrThrow("network", null, content);
        } finally {
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
        Cursor cursor = null;
        long id = -1;
        try {
            cursor = readableDatabase.rawQuery("SELECT id FROM network where bssid='" + bssid + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                id = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return id;
    }

    public long addSignal(long id_network, int level, double longitude, double latitude, float accuracy, long timestamp) {
        System.out.println("Add Signal " + id_network);
        ContentValues content = new ContentValues();
        content.put("id_network", id_network);
        content.put("level", level);
        content.put("longitude", longitude);
        content.put("latitude", latitude);
        content.put("accuracy", accuracy);
        content.put("timestamp", timestamp);
        return writableDatabase.insertOrThrow("signal", null, content);
    }

    public List<Position> getPositions(long id_network) {
        List<Position> list;
        Cursor cursor = null;
        try {
            cursor = readableDatabase.rawQuery("SELECT longitude, latitude FROM SIGNAL where id_network=" + id_network, null);
            list = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isLast()) {
                    Position p = new Position(cursor.getDouble(0), cursor.getDouble(1));
                    list.add(p);
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return list;
    }

}
