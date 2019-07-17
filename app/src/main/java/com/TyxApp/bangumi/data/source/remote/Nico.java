package com.TyxApp.bangumi.data.source.remote;

import android.util.SparseArray;

import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.ParseUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class Nico implements BaseBangumiParser {
    private String baseUrl = "http://www.nicotv.me";
    private SparseArray<String> playerUrls = new SparseArray<>();
    private List<String> jiHtml = new ArrayList<>();
    private List<String> searchMoreHtml = new ArrayList<>();
    private static Nico INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();

    private Nico(){}

    public static Nico getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nico();
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
        String url = baseUrl + "/video/search/" + word +".html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Elements sm = document.getElementsByClass("pagination pagination-lg hidden-xs");
                    if (!sm.isEmpty()) {
                        Element e = sm.get(0);
                        for (int i = 1; i < e.children().size(); i++) {
                            String moreUrl = baseUrl + e.child(i).getElementsByTag("a").attr("href");
                            searchMoreHtml.add(moreUrl);
                        }
                    }
                    List<Bangumi> bangumis = parseSearchPageBangumis(document);
                    return bangumis;
                })
                .subscribeOn(Schedulers.io());

    }

    private List<Bangumi> parseSearchPageBangumis(Document document) {
        Element e = document.getElementsByClass("list-unstyled vod-item-img ff-img-215").get(0);
        Elements ee = e.getElementsByClass("image");
        List<Bangumi> bangumis = new ArrayList<>();
        for (Element eee : ee) {
            bangumis.add(parseBangumi(eee));
        }
        if (!searchMoreHtml.isEmpty()) {
            bangumis.add(null);
        }
        return bangumis;
    }

    private Bangumi parseBangumi (Element ee) throws NumberFormatException {
        String id = ee.getElementsByTag("a").attr("href");
        id = id.substring(id.lastIndexOf("/") + 1, id.lastIndexOf("."));
        String cover = ee.getElementsByTag("img").attr("data-original");
        String name = ee.getElementsByTag("img").attr("alt");
        String remark = ee.getElementsByTag("span").text();
        Bangumi bangumi = new Bangumi();
        bangumi.setSoure(BangumiPresistenceContract.BangumiSource.NiICO);
        bangumi.setName(name);
        bangumi.setRemarks(remark);
        bangumi.setCover(cover);
        bangumi.setVod_id(Integer.valueOf(id));
        return bangumi;
    }

    @Override
    public Observable<List<Bangumi>> nextSearchResult() {
        String nextUrl = searchMoreHtml.remove(0);
        return Observable.just(nextUrl)
                .compose(ParseUtil.html2Transformer())
                .map(this::parseSearchPageBangumis)
                .subscribeOn(Schedulers.io());
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
