package com.labs.dm.wifi_radar.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.labs.dm.wifi_radar.R;
import com.labs.dm.wifi_radar.db.DBManager;
import com.labs.dm.wifi_radar.pojo.MyProperties;
import com.labs.dm.wifi_radar.pojo.Position;
import com.labs.dm.wifi_radar.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, LocationListener {
    private static final int SETTINGS_CODE = 1;
    private static final int NOTIFICATION_EX = 1;
    private final Handler handler = new Handler();
    private WifiManager wifi;
    private ListView lv;
    private ToggleButton tgl;
    private List<ScanResult> results;
    private final Set<String> ssid = new HashSet<>();
    private final List<Map<String, String>> list = new ArrayList();
    private SimpleAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private NotificationManager notificationManager;
    private Runnable runnableCode;
    private LocationManager locationManager;
    private DBManager db;
    private final Map<String, Position> map = new HashMap<>();
    private MyProperties props;
    private Location location;
    private boolean switchedOnWifi;

    @Override
    protected void onDestroy() {
        if (switchedOnWifi) {
            wifi.setWifiEnabled(false);
            Log.i("", "Switch off WIFI");
        }
        super.onDestroy();
    }

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
            Toast.makeText(getApplicationContext(), "Wifi is disabled. Switching on...", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
            switchedOnWifi = true;
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
                    item.put("info", WifiManager.calculateSignalLevel(result.level, 100) + "%, ch" + String.valueOf(Utils.toChannel(result.frequency)));
                    item.put("other", result.capabilities);
                    item.put("bssid", result.BSSID);
                    list.add(item);
                    ssid.add(result.BSSID);

                    Location location = getLastBestLocation();

                    Position current = new Position(location.getLatitude(), location.getLongitude());

                    if (map.containsKey(result.BSSID)) {
                        Position pos = map.get(result.BSSID);
                        if (Math.abs(Utils.calculateDistance(current, pos)) > 1.0d) {
                            addSignalItem(result, location, current);
                        }
                    } else {
                        addSignalItem(result, location, current);
                    }

                }
                setTitle("Found " + list.size() + "/" + ssid.size());
                notification(getTitle());
                adapter.notifyDataSetChanged();
            }


        };

        runnableCode = new Runnable() {
            @Override
            public void run() {
                wifi.startScan();
                handler.postDelayed(this, props.getScanInterval());
            }
        };

        onClick(null);
        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.wifi;
        CharSequence tickerText = "Wifi";
        long when = System.currentTimeMillis();

        notification(icon, tickerText, when);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = new DBManager(this);
        loadProps();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, props.getNetMinTime(), props.getNetMinDist(), this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, props.getGpsMinTime(), props.getGpsMinDist(), this);
    }

    Notification notification;
    PendingIntent contentIntent;
    CharSequence contentTitle = "Wifi Radar";
    CharSequence contentText;

    private void notification(int icon, CharSequence tickerText, long when) {
        notification = new Notification(icon, tickerText, when);

        Context context = getApplicationContext();
        contentText = "Hello World!";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle,
                contentText, contentIntent);

        notificationManager.notify(NOTIFICATION_EX, notification);
    }

    private void notification(CharSequence contentText) {
        notification.setLatestEventInfo(getApplicationContext(), contentTitle,
                contentText, contentIntent);

        notificationManager.notify(NOTIFICATION_EX, notification);

    }

    private void loadProps() {
        props = new MyProperties();
        props.load(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
    }

    private void addSignalItem(ScanResult result, Location location, Position current) {
        long id = db.getIdNetwork(result.BSSID);
        if (id == -1) {
            id = db.addNetwork(result.SSID, result.BSSID, Utils.toChannel(result.frequency), result.capabilities);
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
                startActivityForResult(intent, SETTINGS_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_CODE) {
            if (resultCode == RESULT_OK) {
                loadProps();
            }
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


    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long gpsTime = 0;
        if (locationGPS != null) {
            gpsTime = locationGPS.getTime();
        }

        long networkTime = 0;

        if (locationNet != null) {
            networkTime = locationNet.getTime();
        }
        if ((networkTime - gpsTime) > 60000) {
            return locationNet;
        } else {
            return locationGPS;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        System.out.println("GPS: Location Changed" + location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("GPS: Status Changed" + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("GPS: Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("GPS: Disabled");
    }
}