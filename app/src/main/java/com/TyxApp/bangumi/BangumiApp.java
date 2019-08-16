package com.TyxApp.bangumi;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.server.DownloadServer;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class BangumiApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequestUtil.init();
        AppDatabase.init(getApplicationContext());
        appContext = getApplicationContext();

        //playerbaseInit
        PlayerLibrary.init(this);
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        String decodePlanName = PreferenceUtil.getString(getString(R.string.key_decoder_plan), VideoPlayerEvent.DECODE_PLAN.PLAN_NAME_MEDIA);
        if (decodePlanName.equals(VideoPlayerEvent.DECODE_PLAN.PLAN_NAME_IJK)) {
            IjkPlayer.init(this);
        }
        checkDownLoadTask();

    }

    private void autoRecoverTask() {
        boolean isAutoDownload = PreferenceUtil.getBollean(getString(R.string.key_auto_recover_download), true);
        boolean havePermission = ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (isAutoDownload && havePermission && NetworkUtils.isWifiConnected(appContext)) {
            AppDatabase.getInstance()
                    .getVideoDownloadStackDao()
                    .getUnfinishedTasks()
                    .filter(tasks -> !tasks.isEmpty())//有未完成任务就启动下载服务
                    .subscribe(tasks -> {
                        Intent intent = new Intent(appContext, DownloadServer.class);
                        intent.putExtra(DownloadServer.INTENT_KEY, true);
                        appContext.startService(intent);
                    });
        }
    }


    /**
     * 把用户手动删除的视频移除出数据库
     *
     */
    private void checkDownLoadTask() {
        VideoDownloadTaskDao taskDao = AppDatabase.getInstance().getVideoDownloadStackDao();
        taskDao.getRxDownloadTasks()
                .toFlowable()
                .flatMap(tasks -> Flowable.fromIterable(tasks))
                .filter(task -> {
                    if (task.getState() == DownloadServer.STATE_DOWNLOADING) {//有为下载中状态的任务一般为服务被杀死并且没有调用onDestroy, 要手动置为暂停。
                        task.setState(DownloadServer.STATE_PAUSE);
                        taskDao.update(task);
                    }
                    return !new File(task.getPath()).exists();
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        task -> taskDao.delete(task),
                        throwable -> LogUtil.i(throwable.toString()),
                        () -> autoRecoverTask());//检查完判断是否启动下载服务
    }
}
