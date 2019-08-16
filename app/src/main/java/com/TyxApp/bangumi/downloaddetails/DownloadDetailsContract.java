package com.TyxApp.bangumi.downloaddetails;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;

import java.util.List;

public interface DownloadDetailsContract {
    interface Presenter extends BasePresenter {
        void getTasks(String bangumiId, String sourch);

        void deleteTask(VideoDownloadTask task);

        void deleteTasks(List<VideoDownloadTask> tasks);

    }

    interface View extends BaseView {
        void showTasks(List<VideoDownloadTask> tasks);

    }
}
