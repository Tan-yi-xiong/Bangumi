package com.TyxApp.bangumi.downloaddetails;

import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.util.ExceptionUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadDetailsPresenter implements DownloadDetailsContract.Presenter {
    private DownloadDetailsContract.View mView;
    private VideoDownloadTaskDao mTaskDao;
    private CompositeDisposable mDisposable;

    public DownloadDetailsPresenter(DownloadDetailsContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空 DownloadDetailsPresenter");
        mView = view;
        mDisposable = new CompositeDisposable();
        mTaskDao = AppDatabase.getInstance().getVideoDownloadStackDao();
    }

    @Override
    public void getTasks(int bangumiId, String sourch) {
        mDisposable.add(mTaskDao.getRxDownloadTasks(bangumiId, sourch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tasks -> mView.showTasks(tasks),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void deleteTask(VideoDownloadTask task) {
        mDisposable.add(Single.just(task)
                .map(t -> {
                    File file = new File(t.getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                    return mTaskDao.delete(t);
                })
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void deleteTasks(List<VideoDownloadTask> tasks) {
        mDisposable.add(mTaskDao.deleteTasks(tasks)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
        mView = null;
    }
}
