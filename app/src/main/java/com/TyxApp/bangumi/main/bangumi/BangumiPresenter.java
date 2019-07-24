package com.TyxApp.bangumi.main.bangumi;


import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BangumiPresenter implements BangumiContract.Presenter {
    private BaseBangumiParser banghumiParser;
    private BangumiContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private boolean isFristLoadingData;

    public BangumiPresenter(BaseBangumiParser banghumiParser, BangumiContract.View view) {
        ExceptionUtil.checkNull(banghumiParser, "BangumiPresenter modle解析不能为空");
        ExceptionUtil.checkNull(view, "BangumiPresenter view不能为空");

        isFristLoadingData = true;
        this.banghumiParser = banghumiParser;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onResume() {
        if (isFristLoadingData) {
            populaterBangumi();
            isFristLoadingData = false;
        }
    }

    @Override
    public void onDestory() {
        banghumiParser.onDestroy();
        mView = null;
        mCompositeDisposable.dispose();
    }

    @Override
    public void populaterBangumi() {
        //加载主页数据
        List<List<Bangumi>> homeBangumis = new ArrayList<>();
        mCompositeDisposable.add(banghumiParser.getHomePageBangumiData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homeBangumis::add,

                        throwable -> mView.showResultError(throwable),

                        () -> {
                            //如果不是第一次加载就表明是刷新数据, 调用view层的刷新方法
                            if (isFristLoadingData) {
                                mView.showHomeBangumis(homeBangumis);
                            } else {
                                mView.showNewHomeBangumis(homeBangumis);
                            }
                        }));
    }

    @Override
    public void refreshHomeData() {
        populaterBangumi();
    }
}
