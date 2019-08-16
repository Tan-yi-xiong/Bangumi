package com.TyxApp.bangumi.setting;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey);
        String currentHomeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), "Zzzfun");
        ListPreference homeSourchPreference = findPreference(getString(R.string.key_home_sourch));
        homeSourchPreference.setSummary(currentHomeSourch);
        String decodePlanName = PreferenceUtil.getString(getString(R.string.key_decoder_plan), VideoPlayerEvent.DECODE_PLAN.PLAN_NAME_MEDIA);
        ListPreference decodePlanPreference = findPreference(getString(R.string.key_decoder_plan));
        decodePlanPreference.setSummary(decodePlanName);
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
            ListPreference homeSourchPreference = findPreference(key);
            homeSourchPreference.setSummary(homeSourchPreference.getEntry());
        } else if (key.equals(getString(R.string.key_decoder_plan))) {
            ListPreference DecodePianPreference = findPreference(key);
            String planName = (String) DecodePianPreference.getEntry();
            DecodePianPreference.setSummary(planName);
            int planId = PlayerConfig.DEFAULT_PLAN_ID;
            if (planName.equals(VideoPlayerEvent.DECODE_PLAN.PLAN_NAME_IJK)) {
                planId = IjkPlayer.PLAN_ID;
            }
            if (PlayerConfig.getDefaultPlanId() != planId) {
                PlayerConfig.setDefaultPlanId(planId);
            }
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
