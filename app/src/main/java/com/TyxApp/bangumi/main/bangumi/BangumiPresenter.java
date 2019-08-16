package com.TyxApp.bangumi.main.bangumi;


import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BangumiPresenter implements BangumiContract.Presenter {
    private IBangumiParser banghumiParser;
    private BangumiContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    public BangumiPresenter(IBangumiParser banghumiParser, BangumiContract.View view) {
        ExceptionUtil.checkNull(banghumiParser, "BangumiPresenter modle解析不能为空");
        ExceptionUtil.checkNull(view, "BangumiPresenter view不能为空");

        this.banghumiParser = banghumiParser;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mView = null;
        mCompositeDisposable.dispose();
    }

    @Override
    public void populaterBangumi() {
        //加载主页数据
        mCompositeDisposable.add(banghumiParser.getHomePageBangumiData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        groupBangumis -> mView.showHomeBangumis(groupBangumis),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void refreshHomeData() {
        populaterBangumi();
    }
}
