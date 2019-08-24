package com.TyxApp.bangumi.data.source.remote;

import android.content.ContentValues;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.TyxApp.bangumi.BangumiApp;
import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Result;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.parse.IHomePageParse;
import com.TyxApp.bangumi.parse.ISearchParser;
import com.TyxApp.bangumi.player.danmaku.ZzzfunDannukuParser;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.ParseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import okhttp3.Request;

public class ZzzFun implements IHomePageParse, ISearchParser {
    private String baseUrl = "http://111.230.89.165:8089/zapi";
    private int categoryPage;
    private String mCategoryWord;
    private int[] categorItemImages;
    private String[] categorItemNames;
    private List<VideoUrl> videoUrls;

    private ZzzFun() {
        categorItemImages = new int[]{
                R.drawable.zzafun_category_movie,
                R.drawable.zzafun_category_palgantong,
                R.drawable.zzafun_category_zhenren,
                R.drawable.zzafun_category_season_spring,
                R.drawable.zzafun_category_season_summer,
                R.drawable.zzafun_category_season_autumn,
                R.drawable.zzafun_category_season_winter,
                R.drawable.zzafun_category_domestic,
                R.drawable.zzafun_category_teleplay,
                R.drawable.zzafun_category_japan_bangumi};

        categorItemNames = BangumiApp.appContext.getResources().getStringArray(R.array.zzzfun_categor_name);
    }

    public static ZzzFun getInstance() {
        return new ZzzFun();
    }

    /**
     * zzzfun主页分为6部分, URL为 http://api.xaaxhb.com/zapi/type/home.php?t=1
     * t的值分别为9(头部轮播), 42, 1-4,
     */
    @Override
    public Observable<Map<String, List<Bangumi>>> getHomePageBangumiData() {
        final String[] groupName = new String[1];
        return Observable.create((ObservableOnSubscribe<Map<String, List<Bangumi>>>) emitter -> {
            Map<String, List<Bangumi>> groupData = new LinkedHashMap<>();
            for (int i = 0; i < 6; i++) {
                int postions = i;
                groupName[0] = BangumiApp.appContext.getResources().getStringArray(R.array.zzfun_title)[postions];
                if (postions == 0) {
                    postions = 42;
                } else if (postions == 5) {
                    postions = 9;
                }
                String url = baseUrl + "/type/home.php?t=" + postions;
                String jsonData = HttpRequestUtil.getResponseBodyString(url);
                groupData.put(groupName[0], getBangumis(jsonData));
            }
            emitter.onNext(groupData);
        }).subscribeOn(Schedulers.io());
    }

    @Nullable
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
        return Observable.just(word)
                .map(URLEncoder::encode)
                .map(encodeWord -> "http://111.230.89.165:8099/api.php/provvde/vod/?ac=list&wd=" + encodeWord)
                .map(HttpRequestUtil::getResponseBodyString)
                .flatMap(jsonData -> {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    Type type = new TypeToken<List<Bangumi>>() {
                    }.getType();
                    List<Bangumi> bangumis = gson.fromJson(jsonObject.get("list").toString(), type);
                    return Observable.fromIterable(bangumis);
                })
                .map(bangumi -> {
                    bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
                    return bangumi;
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Result<List<Bangumi>>> nextSearchResult() {
        return Observable.just(new Result(true, null));
    }

    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        return Observable.just("http://www.zzzfun.com/vod-detail-id-"+ id +".html")
                .map(url -> {
                    return new Request.Builder().url(url)
                            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; HTC D820u Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.89 Mobile Safari/537.36")
                            .build();
                })
                .map(HttpRequestUtil::getResponseBodyString)
                .map(Jsoup::parse)
                .map(document -> {
                    String niandai = document.getElementsByAttributeValue("itemprop", "uploadDate").get(0).attr("content");
                    String cast = document.getElementsByAttributeValue("itemprop", "actor").get(0).attr("content").replaceAll(",", "\n");
                    String intro = document.getElementsByAttributeValue("itemprop", "description").get(0).attr("content");
                    String jiTotal = document.getElementsByClass("leo-color-a leo-fs-l leo-ellipsis-1").get(0).text().split("\\|")[1].trim();
                    String staff = document.getElementsByClass("leo-ellipsis-1 leo-fs-s leo-lh-ss").get(0).text();
                    return new BangumiInfo(niandai, cast, staff, "", intro, jiTotal);
                })
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  zzinfo"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        return Observable.create((ObservableOnSubscribe<List<TextItemSelectBean>>) emitter -> {
            String url = baseUrl + "/list.php?id=" + id;
            String jiListData = HttpRequestUtil.getResponseBodyString(url);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jiListData, JsonObject.class);
            Type type = new TypeToken<List<ZzzFunJi>>() {
            }.getType();
            List<ZzzFunJi> jiDataList = gson.fromJson(jsonObject.get("result").toString(), type);
            List<TextItemSelectBean> jiList = new ArrayList<>();
            List<VideoUrl> playUrls = new ArrayList<>();
            for (ZzzFunJi zzzFunJi : jiDataList) {
                if (TextUtils.isEmpty(zzzFunJi.ji)) {
                    continue;
                }
                TextItemSelectBean itemSelectBean = new TextItemSelectBean(zzzFunJi.ji);
                jiList.add(itemSelectBean);
                VideoUrl videoUrl = new VideoUrl(baseUrl + "/play.php?url=" + zzzFunJi.id);
                videoUrl.setHtml(false);
                playUrls.add(videoUrl);
            }
            videoUrls = playUrls;
            emitter.onNext(jiList);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        if (videoUrls == null || videoUrls.isEmpty()) {
            return Observable.just(new VideoUrl(true, "http://www.zzzfun.com/index.php/vod-detail-id-" + id + ".html"));
        }
        return Observable.just(videoUrls.get(ji));
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
        String url = baseUrl + "/type/rnd.php";
        return Observable.just(url)
                .map(s -> {
                    String jsonData = HttpRequestUtil.getResponseBodyString(s);
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
            return HttpRequestUtil.getResponseBodyString(seasonNewBangumiUrl);
        } else if ("日本动漫".equals(mCategoryWord)) {
            mCategoryWord = "1";
        } else if ("影视剧".equals(mCategoryWord)) {
            mCategoryWord = "4";
        }
        ContentValues values = new ContentValues();
        values.put("pageNow", categoryPage);
        values.put("tagNames", mCategoryWord);
        String jsonData = HttpRequestUtil.postJosonResult(url, values);
        jsonData = jsonData.substring(jsonData.indexOf("{"), jsonData.lastIndexOf("}") + 1);
        return jsonData;
    }

    @Override
    public Observable<Result<List<Bangumi>>> getNextCategoryBangumis() {
        categoryPage++;
        String url = baseUrl + "/type/list.php";
        return Observable.just(url)
                .map(this::parseCategoryBangumi)
                .map(jsonData -> {
                    List<Bangumi> bangumis = getBangumis(jsonData);
                    Result<List<Bangumi> > results = new Result(false, bangumis);
                    if (bangumis == null) {
                        results.setNull(true);
                    }
                    return results;
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
                    String jsonData = HttpRequestUtil.getResponseBodyString(s);
                    JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                    return Observable.fromIterable(jsonArray);
                })
                .map(jsonElement -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    List<Bangumi> bangumis = new Gson().fromJson(jsonObject.getAsJsonArray("seasons").toString(), new TypeToken<List<Bangumi>>() {
                    }.getType());
                    for (Bangumi bangumi : bangumis) {
                        bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.ZZZFUN);
                    }
                    return bangumis;
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Result<BaseDanmakuParser>> getDanmakuParser(String id, int ji) {
        ji++;
        return Observable.just("http://111.230.89.165:8089/zapi/dm.php?id%5B%5D=" + id + "&id%5B%5D=" + ji)
                .map(HttpRequestUtil::getRespondBody)
                .map(responseBody -> {
                    ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
                    loader.load(responseBody.byteStream());
                    BaseDanmakuParser parser = new ZzzfunDannukuParser();
                    parser.load(loader.getDataSource());
                    return new Result<>(false, parser);
                })
                .subscribeOn(Schedulers.io());
    }

    class ZzzFunJi {
        public String ji;
        public String id;
    }
}
