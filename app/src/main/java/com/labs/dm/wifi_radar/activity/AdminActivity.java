package com.labs.dm.wifi_radar.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.labs.dm.wifi_radar.R;
import com.labs.dm.wifi_radar.pojo.Position;
import com.labs.dm.wifi_radar.utils.Utils;

import java.util.Date;

public class AdminActivity extends Activity implements View.OnClickListener {

    private Button button;
    private TextView textView;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        button = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView2);
        textView.clearComposingText();
        button.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        StringBuilder text = new StringBuilder();

        text.append("GPS:").append("\n");
        text.append(locationGPS.getLongitude()).append(":");
        text.append(locationGPS.getLatitude()).append("\n");
        text.append("time: ").append(new Date(locationGPS.getTime())).append("\n");
        text.append("acc: ").append(locationGPS.getAccuracy()).append("\n\n");

        text.append("Network:").append("\n");
        text.append(locationNet.getLongitude()).append(":");
        text.append(locationNet.getLatitude()).append("\n");
        text.append("time: ").append(new Date(locationNet.getTime())).append("\n");
        text.append("acc: ").append(locationNet.getAccuracy()).append("\n");

        text.append("\n");
        text.append("Distance GPS vs Network [m]: ");
        text.append(Utils.calculateDistance(new Position(locationGPS.getLongitude(), locationGPS.getLatitude()), new Position(locationNet.getLongitude(), locationNet.getLatitude()))).append("\n");
        text.append("Difference GPS vs Network [s]: ");
        text.append((locationGPS.getTime() - locationNet.getTime()) / 1000f).append("\n");

        text.append("Providers").append("\n");
        for (String provider : locationManager.getAllProviders()) {
            text.append(provider).append(" status=").append(locationManager.isProviderEnabled(provider)).append("\n");
        }

        textView.setText(text.toString());
    }
}
