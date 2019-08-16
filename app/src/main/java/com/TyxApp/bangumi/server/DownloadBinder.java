package com.TyxApp.bangumi.server;


import com.TyxApp.bangumi.data.bean.VideoDownloadTask;

public interface DownloadBinder {
    void addTask(String bangumiId, String sourch, String videoUrl, String fileName);

    void addTask(VideoDownloadTask task);

    void start();

    void pause();

    void setProgressUpdateLintener(DownloadServer.onProgressUpdateLintener progressUpdateLintener);

    void setInterruptTask(VideoDownloadTask task);

}
