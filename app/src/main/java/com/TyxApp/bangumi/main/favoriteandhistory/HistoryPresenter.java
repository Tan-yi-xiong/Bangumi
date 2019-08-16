package com.TyxApp.bangumi.main.favoriteandhistory;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements FavoriteAndHistoryContract.Presenter {
    private FavoriteAndHistoryContract.View mView;
    private BangumiDao mBangumiDao;
    private CompositeDisposable mDisposable;

    public HistoryPresenter(FavoriteAndHistoryContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空 HistoryPresenter");
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
    }

    @Override
    public void getBangumis() {
        mDisposable.add(mBangumiDao.getHistoryBangumi()
                .toFlowable()
                .flatMap(Flowable::fromIterable)
                .filter(bangumi -> bangumi.getHistoryTime() != 0)//时间等于0代表被移除出历史记录
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (bangumis.isEmpty()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showBangumis(bangumis);
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void removeBangumi(String id, String source) {
        mDisposable.add(Single.create(emitter -> mBangumiDao.updatetime(id, source, 0))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void revocationRemoveBangumi(Bangumi bangumi) {
        mDisposable.add(mBangumiDao.insertBangumi(bangumi)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }
}
