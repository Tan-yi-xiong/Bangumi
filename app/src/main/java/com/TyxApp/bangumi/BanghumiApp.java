package com.TyxApp.bangumi;

import android.app.Application;
import android.content.Context;

import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;

public class BanghumiApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequestUtil.init();
        AppDatabase.init(getApplicationContext());
        appContext = getApplicationContext();

        //playerbaseInit
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        PlayerLibrary.init(this);
        IjkPlayer.init(this);
    }
}
