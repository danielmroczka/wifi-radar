package com.labs.dm.wifi_radar.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.labs.dm.wifi_radar.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference pref;
    private String summaryStr;
    private String prefixStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        //SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
        //sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Get the current summary
        pref = findPreference(key);
        summaryStr = (String) pref.getSummary();

        //Get the user input data
        prefixStr = sharedPreferences.getString(key, "");

        //Update the summary with user input data
        pref.setSummary(summaryStr.concat(": [").concat(prefixStr).concat("]"));
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
