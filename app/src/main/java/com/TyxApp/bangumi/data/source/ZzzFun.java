package com.TyxApp.bangumi.data.source;

import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ZzzFun implements BaseBangumiParser{
    private String baseUrl = "http://api.xaaxhb.com/zapi";

    /**
     * zzzfun主页分为5部分, URL为 http://api.xaaxhb.com/zapi/type/home.php?t=1
     * t的值分别为42, 1-4
     */
    @Override
    public Observable<List<Bangumi>> getHomePageBangumiData() {
        return Observable.range(0, 5)
                .map(integer -> {
                    if (integer == 0) {
                        //zzzfun的第一栏数据url的t是42
                        integer = 42;
                    }
                    String url = baseUrl + "/type/home.php?t=" + integer;
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    Type type = new TypeToken<List<Bangumi>>(){}.getType();
                    List<Bangumi> bangumis = gson.fromJson(jsonObject.get("result").toString(), type);
                    return bangumis;
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getHomePageHeadeBangumiData() {
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
    public Observable<String> getDescribe(int id) {
        return null;
    }

    @Override
    public Observable<List<String>> getJiTitle(int id) {
        return null;
    }

    @Override
    public Observable<String> getplayerUrl(int id, int ji) {
        return null;
    }
}
