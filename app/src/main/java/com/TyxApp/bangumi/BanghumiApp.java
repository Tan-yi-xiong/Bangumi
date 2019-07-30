package com.TyxApp.bangumi;

import android.app.Application;
import android.content.Context;

import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;

import java.io.File;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

        checkDownLoadTask();
    }

    /**
     * 把用户手动删除的视频移除出数据库
     */
    private void checkDownLoadTask() {
        VideoDownloadTaskDao taskDao = AppDatabase.getInstance().getVideoDownloadStackDao();
        taskDao.getDownloadTasks()
                .toFlowable()
                .flatMap(tasks -> Flowable.fromIterable(tasks))
                .filter(task -> !new File(task.getPath()).exists())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        task -> taskDao.delete(task),
                        throwable -> LogUtil.i(throwable.toString()));
    }
}
