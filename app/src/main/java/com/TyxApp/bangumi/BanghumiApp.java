package com.TyxApp.bangumi;

import android.app.Application;
import android.content.Context;

import com.TyxApp.bangumi.util.HttpRequestUtil;

public class BanghumiApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequestUtil.init();
        appContext = getApplicationContext();
    }
}
