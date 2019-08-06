package com.TyxApp.bangumi.data.source.remote;

import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Results;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

public class Dilidili implements BaseBangumiParser {
    private static Dilidili INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();
    private static String BASE_URL = "http://go.dilidili.club";
    private int mCategoryPage;
    private String mCategoryWord;
    private SparseArray<List<String>> videoUrlsCollect;

    private Dilidili() {
        videoUrlsCollect = new SparseArray<>();
    }

    public static Dilidili getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Dilidili();
        }
        INSTANCECOUNTER.getAndIncrement();
        return INSTANCE;
    }

    @Override
    public void onDestroy() {
        videoUrlsCollect.clear();
        if (INSTANCECOUNTER.getAndDecrement() == 1) {
            INSTANCE = null;
        }
    }

    @Override
    public Observable<List<Bangumi>> getHomePageBangumiData() {
        String[] homeUrls = {
                BASE_URL + "/bangumi/newest?size=9",//最新番剧
                BASE_URL + "/bangumi_by_rank?size=9",//番剧排行
                BASE_URL + "/get_recommend_bangumi?size=9",//推荐番剧
                BASE_URL + "/banners?seat=0"//轮播图
        };
        return Observable.fromArray(homeUrls)
                .concatMap(url -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    if (url.equals(homeUrls[3])) {
                        Gson gson = new Gson();
                        return Observable.just(jsonData)
                                .flatMap(json -> {
                                    JsonArray bangumiArray = new JsonParser().parse(jsonData)
                                            .getAsJsonObject()
                                            .get("data")
                                            .getAsJsonArray();
                                    return Observable.fromIterable(bangumiArray);
                                })
                                .filter(jsonElement -> jsonElement.getAsJsonObject().get("id").getAsInt() != 8)//广告id为8
                                .map(jsonElement -> {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject().get("bangumi").getAsJsonObject();
                                    jsonObject.addProperty("img", jsonElement.getAsJsonObject().get("img").getAsString());//轮播图地址
                                    Bangumi bangumi = gson.fromJson(jsonObject.toString(), Bangumi.class);
                                    bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.DILIDLI);
                                    return bangumi;
                                })
                                .toList()
                                .toObservable();
                    }
                    return Observable.fromIterable(parshBangumis(jsonData))
                            .map(bangumi -> {
                                bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.DILIDLI);
                                return bangumi;
                            })
                            .take(6)
                            .toList()
                            .toObservable();

                })
                .subscribeOn(Schedulers.io());
    }

    @Nullable
    private List<Bangumi> parshBangumis(String jsonData) {
        Type type = new TypeToken<JsonResult<List<Bangumi>>>() {
        }.getType();
        JsonResult<List<Bangumi>> jsonResult = new Gson().fromJson(jsonData, type);
        return jsonResult.data;
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        return null;
    }

    @Override
    public Observable<Results> nextSearchResult() {
        return null;
    }

    private ObservableTransformer<JsonElement, Bangumi> parseSearchBangumi() {
        return observable -> observable.map(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Bangumi bangumi = new Bangumi(
                    jsonObject.get("typeid").getAsInt(),
                    BangumiPresistenceContract.BangumiSource.DILIDLI,
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("litpic").getAsString());
            bangumi.setRemarks(jsonObject.get("writer").getAsString());
            return bangumi;
        });
    }

    @Override
    public Observable<String> getIntor(int id) {
        return Observable.just(BASE_URL + "/bangumi/seasons?arctype_id=1333")
                .map(HttpRequestUtil::getGetRequestResponseBodyString)
                .map(jsonData -> {
                    JsonObject jsonObject = new JsonParser().parse(jsonData)
                            .getAsJsonObject()
                            .getAsJsonArray("data")
                            .get(0)
                            .getAsJsonObject();
                    return jsonObject.get("description").getAsString();
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(int id) {
        List<String> videoUrls = new ArrayList<>();
        return Observable.just(BASE_URL + "/bangumi/episodes?arctype_id=" + id + "&size=1000")
                .map(HttpRequestUtil::getGetRequestResponseBodyString)
                .flatMap(jsonData -> {
                    Type type = new TypeToken<JsonResult<List<Ji>>>() {}.getType();
                    JsonResult<List<Ji>> jsonResult = new Gson().fromJson(jsonData, type);

                    return Observable.fromIterable(jsonResult.data);
                })
                .map(ji -> {
                    TextItemSelectBean itemSelectBean = new TextItemSelectBean("第" + ji.name + "话");
                    videoUrls.add(BASE_URL + "/playurls?archive_id=" + ji.playerId);
                    return itemSelectBean;
                })
                .toList()
                .toObservable()
                .doOnComplete(() -> videoUrlsCollect.append(id, videoUrls))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(int id, int ji) {
        return Observable.just(videoUrlsCollect.get(id).get(ji))
                .map(HttpRequestUtil::getGetRequestResponseBodyString)//获取json数据
                .map(jsonData -> {
                    Type type = new TypeToken<JsonResult<List<String>>>() {
                    }.getType();
                    JsonResult<List<String>> videoUrls = new Gson().fromJson(jsonData, type);
                    VideoUrl videoUrl = new VideoUrl();
                    if (videoUrls.data.isEmpty()) {
                        videoUrl.setUrl("http://www.dilidili.name/");
                        videoUrl.setHtml(true);
                    } else {
                        videoUrl.setUrl(videoUrls.data.get(0));
                    }
                    return videoUrl;
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        return Observable.just(BASE_URL + "/bangumi/similar?arctype_id=" + id)
                .map(url -> HttpRequestUtil.getGetRequestResponseBodyString(url))
                .map(this::parshBangumis)
                .flatMap(bangumis -> Observable.fromIterable(bangumis))
                .compose(addSourch())
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        mCategoryPage = 1;
        mCategoryWord = category;
        return Observable.just(category)
                .map(categoryWord -> {
                    String url = null;
                    if (categoryWord.equals("最新更新")) {
                        url = BASE_URL + "/bangumi/newest?size=15&page=" + mCategoryPage;
                    } else if (categoryWord.equals("番剧排行")) {
                        url = BASE_URL + "/bangumi_by_rank?size=15&page=" + mCategoryPage;
                    } else if (categoryWord.equals("番剧推荐")) {
                        url = BASE_URL + "/get_recommend_bangumi?size=15&page=" + mCategoryPage;
                    } else {
                        categoryWord = URLEncoder.encode(categoryWord, "UTF-8");
                        url = BASE_URL + "/bangumi_by_category?category=" + categoryWord + "&size=15&page=" + mCategoryPage;
                    }
                    return url;
                })
                .flatMap(url -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    return Observable.fromIterable(parshBangumis(jsonData));
                })
                .compose(addSourch())
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Results> getNextCategoryBangumis() {
        mCategoryPage++;
        return Observable.just(mCategoryWord)
                .map(categoryWord -> {
                    String url = null;
                    if (categoryWord.equals("最新更新")) {
                        url = BASE_URL + "/bangumi/newest?size=15&page=" + mCategoryPage;
                    } else if (categoryWord.equals("番剧排行")) {
                        url = BASE_URL + "/bangumi_by_rank?size=15&page=" + mCategoryPage;
                    } else if (categoryWord.equals("番剧推荐")) {
                        url = BASE_URL + "/get_recommend_bangumi?size=15&page=" + mCategoryPage;
                    } else {
                        categoryWord = URLEncoder.encode(categoryWord, "UTF-8");
                        url = BASE_URL + "/bangumi_by_category?category=" + categoryWord + "&size=15&page=" + mCategoryPage;
                    }
                    return url;
                })
                .flatMap(url -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    List<Bangumi> bangumis = parshBangumis(jsonData);
                    if (bangumis == null || bangumis.isEmpty()) {
                        return Observable.just(new Results(true, bangumis));
                    } else {
                        return Observable.fromIterable(bangumis)
                                .compose(addSourch())
                                .toList()
                                .toObservable()
                                .map(bangumiList -> new Results(false, bangumiList));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private ObservableTransformer<Bangumi, Bangumi> addSourch() {
        return observable -> observable.map(bangumi -> {
            bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.DILIDLI);
            return bangumi;
        });
    }

    @Override
    public Observable<List<CategorItem>> getCategorItems() {
        return Observable.just(BASE_URL + "/categories")
                .map(url -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    Type type = new TypeToken<JsonResult<List<CategorItem>>>() {
                    }.getType();
                    JsonResult<List<CategorItem>> jsonResult = new Gson().fromJson(jsonData, type);
                    return jsonResult.data;
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<List<Bangumi>>> getBangumiTimeTable() {
        String timeTableUrl = BASE_URL + "/home";
        return Observable.just(timeTableUrl)
                .map(url -> {
                    String jsonData = HttpRequestUtil.getGetRequestResponseBodyString(url);
                    Type type = new TypeToken<JsonResult<List<List<Bangumi>>>>() {
                    }.getType();
                    JsonResult<List<List<Bangumi>>> jsonResult = new Gson().fromJson(jsonData, type);
                    return jsonResult.data;
                })
                .subscribeOn(Schedulers.io());
    }

    class JsonResult<T> {
        int code;
        String message;
        T data;
    }

    class Ji {
        @SerializedName("writer")
        String name;
        @SerializedName("id")
        int playerId;
    }

}
