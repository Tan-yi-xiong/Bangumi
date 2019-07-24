package com.TyxApp.bangumi.main.myfavorite;

import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.function.BiConsumer;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {
    private BangumiDao mBangumiDao;
    private CompositeDisposable mDisposable;
    private MyFavoriteContract.View mView;

    public MyFavoritePresenter(MyFavoriteContract.View view) {
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getMyFavoriteBangumis() {
        mDisposable.add(mBangumiDao.getFavoriteBangumi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (bangumis.isEmpty()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showMyFavoriteBangumis(bangumis);
                            }

                        },
                        throwable -> mView.showResultError(throwable)));

    }

    @Override
    public void removeMyFavoriteBangumi(int id, String source) {
       mDisposable.add(mBangumiDao.updateFavoriteState(id, source, false)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
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
