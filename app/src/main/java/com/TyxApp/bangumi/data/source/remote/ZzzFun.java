package com.TyxApp.bangumi.data.source.remote;

import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class ZzzFun implements BaseBangumiParser {
    private String baseUrl = "http://api.xaaxhb.com/zapi";
    private List<String> playerUrls;
    private static ZzzFun INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();

    private ZzzFun(){}

    public static ZzzFun getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZzzFun();
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


    /**
     * zzzfun主页分为6部分, URL为 http://api.xaaxhb.com/zapi/type/home.php?t=1
     * t的值分别为9(头部轮播), 42, 1-4,
     */
    @Override
    public Observable<List<Bangumi>> getHomePageBangumiData() {
        return Observable.range(0, 6)
                .map(integer -> {
                    if (integer == 0) {
                        integer = 42;
                    }
                    if (integer == 5) {
                        integer = 9;
                    }
                    String url = baseUrl + "/type/home.php?t=" + integer;
                    return getBangumis(url);
                })
                .subscribeOn(Schedulers.io());
    }

    private List<Bangumi> getBangumis(String url) throws IOException {
        String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
        Gson gson = new Gson();
        //只要result字段的数据
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        Type type = new TypeToken<List<Bangumi>>(){}.getType();
        List<Bangumi> bangumis = gson.fromJson(jsonObject.get("result").toString(), type);
        for (Bangumi bangumi : bangumis) {
            bangumi.setSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
        }
        return bangumis;
    }


    @Override
    public Observable<List<Bangumi>> getSearchResult(final String word) {
        return Observable.create((ObservableOnSubscribe<List<Bangumi>>) emitter -> {
            String w = URLEncoder.encode(word, "UTF-8");
            String searchUrl =
                    "http://111.230.89.165:8099/api.php/provvde/vod/?ac=list&wd=" + w;

            String jsonResultData = HttpRequestUtil.getGetRequestResponseBodyString(searchUrl);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResultData, JsonObject.class);
            Type type = new TypeToken<List<Bangumi>>() {}.getType();
            List<Bangumi> bangumis = gson.fromJson(jsonObject.get("list").toString(), type);
            for (Bangumi bangumi : bangumis) {
                bangumi.setSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
            }
            emitter.onNext(bangumis);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> nextSearchResult() {
        return null;
    }

    @Override
    public Observable<String> getIntor(int id) {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String intorUrl = baseUrl + "/video.php?pp=" + id;
            String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(intorUrl);
            JsonReader reader = new JsonReader(new StringReader(jsonData));
            reader.beginObject();
            String intor = "暂无简介";
            while (reader.hasNext()) {
                if ("neirong".equals(reader.nextName())) {
                    intor = reader.nextString();
                    intor = intor.replaceAll("</?[a-zA-Z]+/?>", "");
                } else {
                    reader.skipValue();
                }
            }
            emitter.onNext(intor);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(int id) {
        if (playerUrls != null) {
            if (!playerUrls.isEmpty()) {
                playerUrls.clear();
            }
        } else {
            playerUrls = new ArrayList<>();
        }
        return Observable.create((ObservableOnSubscribe<List<TextItemSelectBean>>) emitter -> {
            String url = baseUrl + "/list.php?id=" + id;
            String jiListData = HttpRequestUtil.getGetRequestResponseBodyString(url);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jiListData, JsonObject.class);
            Type type = new TypeToken<List<ZzzFunJi>>(){}.getType();
            List<ZzzFunJi> jiDataList = gson.fromJson(jsonObject.get("result").toString(), type);
            List<TextItemSelectBean> jiList = new ArrayList<>();
            for (ZzzFunJi zzzFunJi : jiDataList) {
                TextItemSelectBean itemSelectBean = new TextItemSelectBean(zzzFunJi.ji);
                jiList.add(itemSelectBean);
                playerUrls.add(baseUrl + "/play.php?url=" + zzzFunJi.id);
            }
            emitter.onNext(jiList);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<String> getplayerUrl(int id, int ji) {
        if (playerUrls == null || playerUrls.isEmpty()) {
            throw new IllegalArgumentException("逻辑错误了");
        }
        return Observable.just(playerUrls.get(ji));
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        String url = baseUrl + "/type/rnd.php";
        return Observable.just(url)
                .map(this::getBangumis)
                .subscribeOn(Schedulers.io());
    }

    class ZzzFunJi {
        public String ji;
        public String id;
    }
}
