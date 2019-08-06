package com.TyxApp.bangumi.main.cachevideo;

import android.widget.Toast;

import com.TyxApp.bangumi.BanghumiApp;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.server.DownloadServer;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CacheVideoPresenter implements CacheVideoContract.Presenter {
    private CompositeDisposable mDisposable;
    private CacheVideoContract.View mView;
    private BangumiDao mBangumiDao;
    private VideoDownloadTaskDao mTaskDao;

    public CacheVideoPresenter(CacheVideoContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空 CacheVideoPresenter");
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mTaskDao = AppDatabase.getInstance().getVideoDownloadStackDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getDownoadBangumis() {
        mDisposable.add(mBangumiDao.getDownLoadBangumi()
                .toFlowable()
                .flatMap(Flowable::fromIterable)
                .map(bangumi -> {//检查该番下载认为是否为空
                    List<VideoDownloadTask> tasks = mTaskDao.getDownloadTasks(bangumi.getVodId(), bangumi.getVideoSoure());
                    if (tasks.isEmpty()) {
                        bangumi.setDownLoad(false);
                        mBangumiDao.update(bangumi);
                    }
                    return bangumi;
                })
                .filter(Bangumi::isDownLoad)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (!bangumis.isEmpty()) {
                                mView.showDownoadBangumis(bangumis);
                            } else {
                                mView.showResultEmpty();
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void removeDownoadBangumi(Bangumi bangumi) {
        bangumi.setDownLoad(false);
        Observable.just(bangumi)
                .observeOn(Schedulers.io())
                .map(b -> {
                    VideoDownloadTask task = mTaskDao.getDownloadTask(DownloadServer.STATE_DOWNLOADING);
                    LogUtil.i(Thread.currentThread().getName());
                    if (task != null) {
                        if (task.getBangumiId() == b.getVodId() && task.getBangumiSourch().equals(b.getVideoSoure())) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(canDelete -> {
                    if (!canDelete) {
                        Toast.makeText(BanghumiApp.appContext, "该番有任务下载中, 请暂停后再操作", Toast.LENGTH_SHORT).show();
                    }
                    return canDelete;
                })
                .observeOn(Schedulers.io())
                .flatMap(canDelete -> Observable.fromIterable(mTaskDao.getDownloadTasks(bangumi.getVodId(), bangumi.getVideoSoure())))
                .map(task -> {
                    File file = new File(task.getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                    mTaskDao.delete(task);
                    return task;
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tasks -> {
                            if (!tasks.isEmpty()) {
                                Toast.makeText(BanghumiApp.appContext, "删除" + tasks.size() + "个任务", Toast.LENGTH_SHORT).show();
                                getDownoadBangumis();
                            }

                        },
                        throwable -> mView.showResultError(throwable));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }
}
