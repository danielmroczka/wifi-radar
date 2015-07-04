package com.labs.dm.wifi_radar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private WifiManager wifi;
    private ListView lv;
    private ToggleButton tgl;
    private List<ScanResult> results;

    private ArrayList<Map<String, String>> arraylist = new ArrayList();
    private SimpleAdapter adapter;
    private BroadcastReceiver br;

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
        this.adapter = new SimpleAdapter(this, arraylist, R.layout.row, new String[]{"ssid", "info", "other"}, new int[]{R.id.ssid, R.id.info, R.id.other});
        lv.setAdapter(this.adapter);
        wifi.startScan();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                if (results != null) {
                    Collections.sort(results, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult lhs, ScanResult rhs) {
                            return rhs.level - lhs.level;
                        }
                    });
                }
                arraylist.clear();

                if (results == null) {
                    return;
                }

                for (ScanResult result:results) {
                    Map<String, String> item = new HashMap();
                    item.put("ssid", result.SSID);
                    item.put("info", wifi.calculateSignalLevel(result.level, 100) + "%, CH" +  String.valueOf(toChannel(result.frequency)));
                    item.put("other", String.valueOf(result));
                    arraylist.add(item);
                    adapter.notifyDataSetChanged();
                }

                //onClick(null);
            }
        };

        onClick(null);

    }

    public void onClick(View view) {
        if (!tgl.isChecked()) {
            unregisterReceiver(br);
        } else {
            registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }



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
}