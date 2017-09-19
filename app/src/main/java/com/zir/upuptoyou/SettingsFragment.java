package com.zir.upuptoyou;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

/**
 * Created by zirco on 17-9-19.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref);
        Preference p = findPreference(getString(R.string.pref_quality_key));
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        p.setSummary(sharedPreferences.getString(getString(R.string.pref_quality_key), "85"));
        p.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please select a number between 0 and 100", Toast.LENGTH_SHORT);

        String sizeKey = getString(R.string.pref_quality_key);
        if (preference.getKey().equals(sizeKey)) {
            String stringSize = ((String) (newValue)).trim();
            try {
                float size = Integer.parseInt(stringSize);
                if (size > 100 || size < 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference p = findPreference(s);
        if(p!=null) {
            if (p instanceof EditTextPreference) {
                p.setSummary(sharedPreferences.getString(s, "85"));
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
