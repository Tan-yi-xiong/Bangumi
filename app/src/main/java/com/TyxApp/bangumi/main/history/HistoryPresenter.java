package com.TyxApp.bangumi.main.history;

import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements HistoryContract.Presenter {
    private HistoryContract.View mView;
    private BangumiDao mBangumiDao;
    private CompositeDisposable mDisposable;

    public HistoryPresenter(HistoryContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空 HistoryPresenter");
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getHistoryBangumis() {
        LogUtil.i("safd");
        mDisposable.add(mBangumiDao.getHistoryBangumi()
                .toFlowable()
                .flatMap(Flowable::fromIterable)
                .filter(bangumi -> bangumi.getTime() != 0)//时间等于0代表被移除出历史记录
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (bangumis.isEmpty()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showHistoryBangumis(bangumis);
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void removeHistoryBangumi(int id, String source) {
        mDisposable.add(Single.create(emitter -> mBangumiDao.updatetime(id, source, 0))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
    }
}
