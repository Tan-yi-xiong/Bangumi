package com.TyxApp.bangumi;

import android.app.Application;

import com.TyxApp.bangumi.util.HttpRequestUtil;

public class BanghumiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequestUtil.init();
    }
}
