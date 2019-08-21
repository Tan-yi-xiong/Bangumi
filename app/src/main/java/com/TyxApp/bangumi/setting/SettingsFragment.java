package com.TyxApp.bangumi.setting;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.util.FormatUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Disposable mDisposable;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey);
        //主页
        String currentHomeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), "Zzzfun");
        ListPreference homeSourchPreference = findPreference(getString(R.string.key_home_sourch));
        homeSourchPreference.setSummary(currentHomeSourch);

        //解码
        String decodePlanName = PreferenceUtil.getString(getString(R.string.key_decoder_plan), VideoPlayerEvent.DECODE_PLAN.PLAN_NAME_MEDIA);
        ListPreference decodePlanPreference = findPreference(getString(R.string.key_decoder_plan));
        decodePlanPreference.setSummary(decodePlanName);

        //版本
        try {
            String version = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName;
            Preference preference = findPreference("versions");
            preference.setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //缓存
        Preference cachePreference = findPreference(getString(R.string.key_clear_cache));
        cachePreference.setSummary(FormatUtil.byteToMB(getGlidecacheSize()));
    }

    private long getGlidecacheSize() {
        File file = Glide.getPhotoCacheDir(requireContext());
        long size = 0;
        if (file != null && file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                size += listFile.length();
            }
        }
        return size;
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
            PlayerConfig.setDefaultPlanId(planId);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (getString(R.string.key_clear_cache).equals(preference.getKey())) {
            mDisposable = Single.just(0)
                    .map(integer -> {
                        Glide.get(requireContext()).clearDiskCache();
                        return getGlidecacheSize();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            size -> preference.setSummary(FormatUtil.byteToMB(size)),
                            throwable -> Toast.makeText(requireContext(), "清除缓存失败", Toast.LENGTH_SHORT).show());

        }
        return super.onPreferenceTreeClick(preference);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}
