package com.TyxApp.bangumi.player;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class PlayerPresenter implements PlayContract.Presenter {
    private PlayContract.View mView;
    private BaseBangumiParser mBangumiParser;
    private CompositeDisposable mDisposable;

    public PlayerPresenter(PlayContract.View view, BaseBangumiParser parser) {
        ExceptionUtil.checkNull(view, "view不能为空  PlayContract");
        ExceptionUtil.checkNull(parser, "presenter不能为空  PlayContract");
        mView = view;
        mBangumiParser = parser;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getBangumiIntro(int bangumiId) {
        mDisposable.add(mBangumiParser.getIntor(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        intro -> mView.showBangumiIntro(intro),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void getBangumiJiList(int bangumiId) {
        mDisposable.add(mBangumiParser.getJiList(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        jiList -> mView.showBangumiJiList(jiList),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void getPlayerUrl(int id, int ji) {
        mDisposable.add(mBangumiParser.getplayerUrl(id, ji)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        playerUrl -> mView.setPlayerUrl(playerUrl),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void getRecommendBangumis(int id) {
        mDisposable.add(mBangumiParser.getRecommendBangumis(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showRecommendBangumis(bangumis),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }
}
