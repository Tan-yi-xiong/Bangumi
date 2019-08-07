package com.TyxApp.bangumi.player;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PlayerPresenter implements PlayContract.Presenter {
    private PlayContract.View mView;
    private BaseBangumiParser mBangumiParser;
    private CompositeDisposable mDisposable;
    private BangumiDao mBangumiDao;

    public PlayerPresenter(PlayContract.View view, BaseBangumiParser parser) {
        ExceptionUtil.checkNull(view, "view不能为空  PlayContract");
        ExceptionUtil.checkNull(parser, "presenter不能为空  PlayContract");
        mView = view;
        mBangumiParser = parser;
        mDisposable = new CompositeDisposable();
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
    }

    @Override
    public void getBangumiInfo(int bangumiId) {
        mDisposable.add(mBangumiParser.getInfo(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        info -> mView.showBangumiInfo(info),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void getBangumiJiList(int bangumiId) {
        mDisposable.add(mBangumiParser.getJiList(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        jiList -> mView.showBangumiJiList(jiList),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void getPlayerUrl(int id, int ji) {
        mDisposable.add(mBangumiParser.getplayerUrl(id, ji)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        videoUrl -> {
                            if (videoUrl.isHtml()) {
                                mView.showSkipDialog(videoUrl.getUrl());
                            } else {
                                mView.setPlayerUrl(videoUrl.getUrl());
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void getRecommendBangumis(int id) {
        mDisposable.add(mBangumiParser.getRecommendBangumis(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showRecommendBangumis(bangumis),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void isFavorite(int id, String sourch) {
        mDisposable.add(mBangumiDao.hasAddToFavorite(id, sourch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFavourite -> mView.changeFavoriteButtonState(isFavourite),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void setFavorite(Bangumi bangumi) {
        mDisposable.add(mBangumiDao.updateFavoriteState(bangumi.getVodId(), bangumi.getVideoSoure(), bangumi.isFavorite())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void setTime(Bangumi bangumi) {
        mDisposable.add(mBangumiDao.insertOrUpdateTime(bangumi)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void setDownload(Bangumi bangumi) {
        mDisposable.add(mBangumiDao.updateDownLoad(bangumi.getVodId(), bangumi.getVideoSoure(), bangumi.isDownLoad())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }


    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mView = null;
        mDisposable.dispose();
        mBangumiParser.onDestroy();
    }
}
