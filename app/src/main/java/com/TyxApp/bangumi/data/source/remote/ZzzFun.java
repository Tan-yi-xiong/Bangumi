package com.TyxApp.bangumi.data.source.remote;

import android.content.ContentValues;
import android.util.SparseArray;

import com.TyxApp.bangumi.BanghumiApp;
import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private String baseUrl = "http://111.230.89.165:8089/zapi";
    private static ZzzFun INSTANCE;
    private SparseArray<List<VideoUrl>> mPlayerUrlsCollect;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();
    private int categoryPage;
    private String mCategoryWord;
    private int[] categorItemImages;
    private String[] categorItemNames;

    private ZzzFun() {
        mPlayerUrlsCollect = new SparseArray<>();
        categorItemImages = new int[]{
                R.drawable.ic_movie,
                R.drawable.ic_palgantong,
                R.drawable.ic_zhenren,
                R.drawable.ic_season_spring,
                R.drawable.ic_season_summer,
                R.drawable.ic_season_autumn,
                R.drawable.ic_season_winter,
                R.drawable.ic_domestic,
                R.drawable.ic_teleplay,
                R.drawable.ic_japan_bangumi};

        categorItemNames = BanghumiApp.appContext.getResources().getStringArray(R.array.zzzfun_categor_name);
    }

    public static ZzzFun getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZzzFun();
        }
        INSTANCECOUNTER.getAndIncrement();
        return INSTANCE;
    }

    @Override
    public void onDestroy() {
        if (mPlayerUrlsCollect.size() > 0) {
            mPlayerUrlsCollect.clear();
        }
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
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    return getBangumis(jsonData);
                })
                .subscribeOn(Schedulers.io());
    }

    private List<Bangumi> getBangumis(String jsonData) {
        Gson gson = new Gson();
        //只要result字段的数据
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        Type type = new TypeToken<List<Bangumi>>() {
        }.getType();
        List<Bangumi> bangumis = gson.fromJson(jsonObject.get("result").toString(), type);
        if (bangumis != null) {
            for (Bangumi bangumi : bangumis) {
                bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
            }
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
            Type type = new TypeToken<List<Bangumi>>() {
            }.getType();
            List<Bangumi> bangumis = gson.fromJson(jsonObject.get("list").toString(), type);
            for (Bangumi bangumi : bangumis) {
                bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
            }
            emitter.onNext(bangumis);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> nextSearchResult() {
        return Observable.empty();
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
                    intor = intor.replaceAll("<.*?>", "");
                } else {
                    reader.skipValue();
                }
            }
            emitter.onNext(intor);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(int id) {
        return Observable.create((ObservableOnSubscribe<List<TextItemSelectBean>>) emitter -> {
            String url = baseUrl + "/list.php?id=" + id;
            String jiListData = HttpRequestUtil.getGetRequestResponseBodyString(url);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jiListData, JsonObject.class);
            Type type = new TypeToken<List<ZzzFunJi>>() {
            }.getType();
            List<ZzzFunJi> jiDataList = gson.fromJson(jsonObject.get("result").toString(), type);
            List<TextItemSelectBean> jiList = new ArrayList<>();
            List<VideoUrl> playUrls = new ArrayList<>();
            for (ZzzFunJi zzzFunJi : jiDataList) {
                TextItemSelectBean itemSelectBean = new TextItemSelectBean(zzzFunJi.ji);
                jiList.add(itemSelectBean);
                VideoUrl videoUrl = new VideoUrl(baseUrl + "/play.php?url=" + zzzFunJi.id);
                videoUrl.setHtml(false);
                playUrls.add(videoUrl);
            }
            mPlayerUrlsCollect.append(id, playUrls);
            emitter.onNext(jiList);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(int id, int ji) {
        List<VideoUrl> playUrls = mPlayerUrlsCollect.get(id);
        if (playUrls != null) {
            return Observable.just(playUrls.get(ji));
        }
        return getJiList(id).map(textItemSelectBeans -> mPlayerUrlsCollect.get(id).get(ji));
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        String url = baseUrl + "/type/rnd.php";
        return Observable.just(url)
                .map(s -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(s);
                    return getBangumis(jsonData);
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        String url = baseUrl + "/type/list.php";
        categoryPage = 1;
        mCategoryWord = category;
        return Observable.just(url)
                .map(this::parseCategoryBangumi)
                .map(this::getBangumis)
                .subscribeOn(Schedulers.io());
    }

    private String parseCategoryBangumi(String url) throws IOException {
        if ("最近更新".equals(mCategoryWord)) {
            String seasonNewBangumiUrl = baseUrl + "/type/hot2.php?page=" + categoryPage;
            return HttpRequestUtil.getGetRequestResponseBodyString(seasonNewBangumiUrl);
        } else if ("日本动漫".equals(mCategoryWord)) {
            mCategoryWord = "1";
        }
        ContentValues values = new ContentValues();
        values.put("pageNow", categoryPage);
        values.put("tagNames", mCategoryWord);
        String jsonData = HttpRequestUtil.postJosonResult(url, values);
        jsonData = jsonData.substring(jsonData.indexOf("{"), jsonData.lastIndexOf("}") + 1);
        return jsonData;
    }

    @Override
    public Observable<List<Bangumi>> getNextCategoryBangumis() {
        categoryPage++;
        String url = baseUrl + "/type/list.php";
        return Observable.just(url)
                .map(this::parseCategoryBangumi)
                .flatMap(jsonData -> {
                    List<Bangumi> bangumis = getBangumis(jsonData);
                    if (bangumis == null) {
                        return Observable.empty();
                    }
                    return Observable.create((ObservableOnSubscribe<List<Bangumi>>) emitter -> emitter.onNext(bangumis));
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<CategorItem>> getCategorItems() {
        return Observable.create(emitter -> {
            List<CategorItem> categorItems = new ArrayList<>();
            for (int i = 0; i < categorItemNames.length; i++) {
                CategorItem item = new CategorItem(categorItemImages[i], categorItemNames[i]);
                categorItems.add(item);
            }
            emitter.onNext(categorItems);
        });
    }

    @Override
    public Observable<List<List<Bangumi>>> getBangumiTimeTable() {
        String url = baseUrl + "/type/week.php";
        return Observable.just(url)
                .concatMap(s -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(s);
                    JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                    return Observable.fromIterable(jsonArray);
                })
                .map(jsonElement -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    List<Bangumi> bangumis = new Gson().fromJson(jsonObject.getAsJsonArray("seasons").toString(), new TypeToken<List<Bangumi>>(){}.getType());
                    for (Bangumi bangumi : bangumis) {
                        bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
                    }
                    return bangumis;
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    class ZzzFunJi {
        public String ji;
        public String id;
    }
}
