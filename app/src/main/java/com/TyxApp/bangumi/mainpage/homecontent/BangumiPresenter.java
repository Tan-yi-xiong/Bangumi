package com.TyxApp.bangumi.mainpage.homecontent;


import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BangumiPresenter<T extends BaseBangumiParser> implements BangumiContract.Presenter {
    private T banghumiParser;
    private BangumiContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    public BangumiPresenter(T banghumiParser, BangumiContract.View view) {
        ExceptionUtil.checkNull(banghumiParser, "BangumiPresenter modle解析不能为空");
        ExceptionUtil.checkNull(view, "BangumiPresenter view不能为空");
        this.banghumiParser = banghumiParser;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onResume() {
        populaterBangumi();
    }

    @Override
    public void onDestory() {
        mCompositeDisposable.dispose();
    }

    @Override
    public void populaterBangumi() {
        //加载主体数据
        List<List<Bangumi>> homeBangumis = new ArrayList<>();
        mCompositeDisposable.add(banghumiParser.getHomePageBangumiData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homeBangumis::add,
                        throwable -> {
                            mView.showBangumiLoadingError();
                            LogUtil.i(throwable.toString());
                        },
                        () -> mView.showHomeBangumi(homeBangumis)));

        //加载头部数据
        mCompositeDisposable.add(banghumiParser.getHomePageHeadeBangumiData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showHeaderData(bangumis),
                        throwable -> {
                            mView.showBangumiLoadingError();
                            LogUtil.i(throwable.toString());
                        }));
    }
}
