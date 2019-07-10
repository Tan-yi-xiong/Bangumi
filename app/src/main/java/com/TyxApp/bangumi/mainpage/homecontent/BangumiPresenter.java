package com.TyxApp.bangumi.mainpage.homecontent;


import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BangumiPresenter<T extends BaseBangumiParser> implements BangumiContract.Presenter {
    private T banghumiParser;
    private BangumiContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private boolean isFristLoadingData;

    public BangumiPresenter(T banghumiParser, BangumiContract.View view) {
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
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        mCompositeDisposable.dispose();
    }

    @Override
    public void onDestory() {
        mView = null;
    }

    @Override
    public void populaterBangumi() {
        //加载主页数据
        List<List<Bangumi>> homeBangumis = new ArrayList<>();
        mCompositeDisposable.add(banghumiParser.getHomePageBangumiData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homeBangumis::add,

                        throwable -> mView.showBangumiLoadingError(throwable),

                        () -> {
                            //如果不是第一次加载就表明是刷新数据, 调用view层的刷新方法
                            if (isFristLoadingData) {
                                mView.showHomeBangumis(homeBangumis);
                            } else {
                                mView.showNewHomeBangumis(homeBangumis);
                            }
                            isFristLoadingData = false;
                        }));
    }

    @Override
    public void refreshHomeData() {
        populaterBangumi();
    }
}
