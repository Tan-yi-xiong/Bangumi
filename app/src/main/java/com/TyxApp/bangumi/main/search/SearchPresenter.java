package com.TyxApp.bangumi.main.search;


import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.SearchWordDao;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchWordDao mSearchWordDao;
    private CompositeDisposable mDisposable;

    SearchPresenter() {
        mSearchWordDao = AppDatabase.getInstance().getSearchWordDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void saveWord(SearchWord searchWord) {
        mDisposable.add(mSearchWordDao.insert(searchWord)
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
