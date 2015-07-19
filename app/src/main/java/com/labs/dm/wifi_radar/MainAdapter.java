package com.labs.dm.wifi_radar;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2015-07-09.
 */
public class MainAdapter extends SimpleAdapter {

    public MainAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView ssid = (TextView) view.findViewById(R.id.ssid);
        TextView info = (TextView) view.findViewById(R.id.info);
        TextView other = (TextView) view.findViewById(R.id.other);
        TextView status = (TextView) view.findViewById(R.id.status);

        Map<String, String> map = (Map<String, String>) getItem(position);
        String cap = map.get("other");

        if (cap == null || cap.isEmpty() || cap.equals("[ESS]")) {
            ssid.setTextColor(Color.GREEN);
        } else {
            ssid.setTextColor(ssid.getTextColors().getDefaultColor());
        }

        status.setText("");
        //other.setText("");
        return view;
    }
}
