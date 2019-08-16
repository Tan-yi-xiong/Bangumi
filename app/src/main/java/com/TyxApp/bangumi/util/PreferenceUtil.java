package com.TyxApp.bangumi.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.TyxApp.bangumi.BangumiApp;

public class PreferenceUtil {

    public static String getString(String key, String defaultValue) {
       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BangumiApp.appContext);
       return preferences.getString(key, defaultValue);
    }

    public static void setString(String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BangumiApp.appContext);
        preferences.edit().putString(key, value).apply();
    }

    public static boolean getBollean(String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BangumiApp.appContext);
        return preferences.getBoolean(key, defaultValue);
    }

    public static void setBollean(String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BangumiApp.appContext);
        preferences.edit().putBoolean(key, value).apply();
    }
}
