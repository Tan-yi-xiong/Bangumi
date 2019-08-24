package com.TyxApp.bangumi.player;

import android.widget.Toast;

import com.TyxApp.bangumi.BangumiApp;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.parse.IVideoParse;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PlayerPresenter implements PlayContract.Presenter {
    private IVideoParse mParser;
    private PlayContract.View mView;
    private CompositeDisposable mDisposable;
    private BangumiDao mBangumiDao;

    public PlayerPresenter(IVideoParse parser, PlayContract.View view) {
        ExceptionUtil.checkNull(view, "view不能为空  PlayContract");
        ExceptionUtil.checkNull(parser, "presenter不能为空  PlayContract");
        mParser = parser;
        mView = view;
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getBangumiInfo(String bangumiId) {
        mDisposable.add(mParser.getInfo(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        info -> mView.showBangumiInfo(info),
                        throwable -> {
                            mView.showBangumiInfo(null);
                            LogUtil.i(throwable.toString());
                        }));
    }

    @Override
    public void getBangumiJiList(String bangumiId) {
        mDisposable.add(mParser.getJiList(bangumiId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        jiList -> mView.showBangumiJiList(jiList),
                        throwable -> mView.showBangumiJiList(null)));
    }

    @Override
    public void getPlayerUrl(String id, int ji) {
        mDisposable.add(mParser.getplayerUrl(id, ji)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        videoUrl -> mView.setPlayerUrl(videoUrl),
                        throwable -> mView.setPlayerUrl(null)));
    }

    @Override
    public void getDanmaku(String id, int ji) {
        mDisposable.add(mParser.getDanmakuParser(id, ji)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mView.setDanmaku(result.getResult()),
                        throwable -> {
                            mView.setDanmaku(null);
                            Toast.makeText(BangumiApp.appContext, "获取弹幕失败", Toast.LENGTH_SHORT).show();
                        }));

    }

    @Override
    public void getRecommendBangumis(String id) {
        mDisposable.add(mParser.getRecommendBangumis(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showRecommendBangumis(bangumis),
                        throwable -> mView.showRecommendBangumis(null)));
    }

    @Override
    public void checkFavorite(String id, String sourch) {
        mDisposable.add(mBangumiDao.hasAddToFavorite(id, sourch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFavourite -> mView.showFavoriteButton(isFavourite),
                        throwable -> LogUtil.i(throwable.toString())));
    }

    @Override
    public void setFavorite(Bangumi bangumi) {
        mDisposable.add(mBangumiDao.updateFavoriteState(bangumi.getVideoId(), bangumi.getVideoSoure(), bangumi.isFavorite())
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
        mDisposable.add(mBangumiDao.updateDownLoad(bangumi.getVideoId(), bangumi.getVideoSoure(), bangumi.isDownLoad())
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
        mBangumiDao = null;
        mParser = null;
        mDisposable.dispose();
    }
}
