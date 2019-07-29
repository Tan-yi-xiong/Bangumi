package com.TyxApp.bangumi.data.source.remote;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;

public class Sakura implements BaseBangumiParser{
    private static Sakura INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();

    private Sakura(){}

    public static Sakura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sakura();
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
    public Observable<VideoUrl> getplayerUrl(int id, int ji) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getNextCategoryBangumis() {
        return null;
    }

    @Override
    public Observable<List<CategorItem>> getCategorItems() {
        return null;
    }

    @Override
    public Observable<List<List<Bangumi>>> getBangumiTimeTable() {
        return null;
    }

}
