package com.labs.dm.wifi_radar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2015-07-09.
 */
public class MainAdpter extends SimpleAdapter {

    public MainAdpter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        Map<String, String> map = (Map<String, String>) getItem(position);
        String cap = map.get("other");
        if (cap == null || cap.isEmpty() || cap.equals("[ESS]")) {
            view.setBackgroundColor(127);
        }
        return view;
    }
}
