package com.TyxApp.bangumi.main.favoriteandhistory;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritePresenter implements FavoriteAndHistoryContract.Presenter {
    private BangumiDao mBangumiDao;
    private CompositeDisposable mDisposable;
    private FavoriteAndHistoryContract.View mView;

    public FavoritePresenter(FavoriteAndHistoryContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空, FavoritePresenter");
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getBangumis() {
        mDisposable.add(mBangumiDao.getFavoriteBangumi()
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
       mDisposable.add(mBangumiDao.updateFavoriteState(id, source, false)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void revocationRemoveBangumi(Bangumi bangumi) {
        mDisposable.add(Single.create(emitter -> emitter.onSuccess(mBangumiDao.update(bangumi)))
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
