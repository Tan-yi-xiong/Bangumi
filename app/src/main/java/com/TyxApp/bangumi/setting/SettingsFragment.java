package com.TyxApp.bangumi.setting;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey);
        String currentHomeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), "Zzzfun");
        ListPreference homeSourchPreference = findPreference(getString(R.string.key_home_sourch));
        homeSourchPreference.setSummary(currentHomeSourch);
        try {
            String version = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName;
            Preference preference = findPreference("versions");
            preference.setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_home_sourch))) {
            ListPreference homeSourchPreference = findPreference(getString(R.string.key_home_sourch));
            homeSourchPreference.setSummary(homeSourchPreference.getEntry());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}
