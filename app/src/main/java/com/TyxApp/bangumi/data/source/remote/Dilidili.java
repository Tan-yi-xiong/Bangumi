package com.TyxApp.bangumi.data.source.remote;

import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;

public class Dilidili implements BaseBangumiParser {
    private static Dilidili INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();

    private Dilidili(){}

    public static Dilidili getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Dilidili();
        }
        INSTANCECOUNTER.getAndIncrement();
        return INSTANCE;
    }

    @Override
    public void onDestroy() {
        if (INSTANCECOUNTER.getAndDecrement() == 1) {
            INSTANCE = null;
        }
    }

    @Override
    public Observable<List<Bangumi>> getHomePageBangumiData() {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> nextSearchResult() {
        return null;
    }

    @Override
    public Observable<String> getIntor(int id) {
        return null;
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(int id) {
        return null;
    }

    @Override
    public Observable<String> getplayerUrl(int id, int ji) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        return null;
    }

}
