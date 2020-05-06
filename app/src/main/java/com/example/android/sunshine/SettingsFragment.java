package com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_sunshine);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = preferenceScreen.getSharedPreferences().getString(preference.getKey(), "");
                setSummary(preference, value);
            }
        }
    }

    private void setSummary(Preference preference, String value) {
        String summary = value;
        if (preference instanceof ListPreference) {
            int index = ((ListPreference) preference).findIndexOfValue(value);
            summary = ((ListPreference) preference).getEntries()[index].toString();
        }
        preference.setSummary(summary);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        setSummary(preference, sharedPreferences.getString(key, ""));
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(requireActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(requireActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }
}
