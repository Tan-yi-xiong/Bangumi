package com.TyxApp.bangumi.data.source;

import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ZzzFun implements BaseBangumiParser {
    private String baseUrl = "http://api.xaaxhb.com/zapi";

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
        Type type = new TypeToken<List<Bangumi>>() {
        }.getType();
        List<Bangumi> bangumis = gson.fromJson(jsonObject.get("result").toString(), type);
        return bangumis;
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
