package com.labs.dm.wifi_radar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private WifiManager wifi;
    private ListView lv;
    private ToggleButton tgl;
    private List<ScanResult> results;
    private Set<String> ssid = new HashSet<>();

    private List<Map<String, String>> list = new ArrayList();
    private SimpleAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private static final int NOTIFICATION_EX = 1;
    private NotificationManager notificationManager;
    private final Handler handler = new Handler();
    private Runnable runnableCode;
    private LocationManager locationManager;
    private DBManager db;
    private Map<String, Position> map = new HashMap<>();

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tgl = (ToggleButton) findViewById(R.id.toggleButton);
        tgl.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.listView);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Wifi is disabled. Please switch on!", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(this, list, R.layout.row, new String[]{"ssid", "info", "other"}, new int[]{R.id.ssid, R.id.info, R.id.other});
        lv.setAdapter(this.adapter);
        wifi.startScan();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                if (results == null) {
                    return;
                }

                list.clear();

                Collections.sort(results, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult lhs, ScanResult rhs) {
                        return rhs.level - lhs.level;
                    }
                });

                for (ScanResult result : results) {
                    Map<String, String> item = new HashMap();
                    item.put("ssid", result.SSID);
                    item.put("info", WifiManager.calculateSignalLevel(result.level, 100) + "%, ch" + String.valueOf(toChannel(result.frequency)));
                    item.put("other", String.valueOf(result));
                    item.put("bssid", result.BSSID);
                    list.add(item);
                    ssid.add(result.SSID);

                    Location location = getLastBestLocation();
                    location.getAccuracy();
                    Position current = new Position(location.getLatitude(), location.getLongitude());

                    if (map.containsKey(result.BSSID)) {
                        Position pos = map.get(result.BSSID);
                        if (Math.abs(GeoUtil.calculateDistance(current, pos)) > 1.0d) {
                            addSignalItem(result, location, current);
                        }
                    } else {
                        addSignalItem(result, location, current);
                    }

                }
                setTitle("Found " + list.size() + "/" + ssid.size());
                adapter.notifyDataSetChanged();
            }


        };

        runnableCode = new Runnable() {
            @Override
            public void run() {
                wifi.startScan();
                handler.postDelayed(this, 5000);
            }
        };

        onClick(null);
        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.wifi;
        CharSequence tickerText = "Wifi";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Context context = getApplicationContext();
        CharSequence contentTitle = "Wifi Radar";
        CharSequence contentText = "Hello World!";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle,
                contentText, contentIntent);

        notificationManager.notify(NOTIFICATION_EX, notification);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = new DBManager(this);
    }

    private void addSignalItem(ScanResult result, Location location, Position current) {
        long id = db.getIdNetwork(result.BSSID);
        if (id == -1) {
            id = db.addNetwork(result.SSID, result.BSSID);
        }
        db.addSignal(id, result.level, current.getLatitude(), current.getLongitude(), location.getAccuracy(), new Date().getTime());
        map.put(result.BSSID, current);
    }

    public void onClick(View view) {
        if (!tgl.isChecked()) {
            handler.removeCallbacks(runnableCode);
        } else {
            handler.post(runnableCode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            notificationManager.cancel(NOTIFICATION_EX);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private int toChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        long GPSLocationTime = 0;
        if (locationGPS != null) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (locationNet != null) {
            NetLocationTime = locationNet.getTime();
        }
        if (GPSLocationTime > NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }
}