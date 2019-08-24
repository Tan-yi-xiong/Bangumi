package com.TyxApp.bangumi.main.bangumi;


import com.TyxApp.bangumi.parse.IHomePageParse;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BangumiPresenter implements BangumiContract.Presenter {
    private IHomePageParse mHomePageParse;
    private BangumiContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    public BangumiPresenter(IHomePageParse homePageParse, BangumiContract.View view) {
        ExceptionUtil.checkNull(homePageParse, "mHomePageParse modle解析不能为空");
        ExceptionUtil.checkNull(view, "BangumiPresenter view不能为空");

        this.mHomePageParse = homePageParse;
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
        mCompositeDisposable.add(mHomePageParse.getHomePageBangumiData()
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
