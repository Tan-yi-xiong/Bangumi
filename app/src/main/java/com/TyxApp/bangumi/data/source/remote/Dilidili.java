package com.TyxApp.bangumi.data.source.remote;

import androidx.annotation.Nullable;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Results;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.main.bangumi.adapter.BannerHomeAdapter;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.ParseUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class Dilidili implements IBangumiParser {
    private static final String BASE_URL = "http://m.dilidili.name";
    private static final String BASE_URL_PC = "http://www.dilidili.name";
    private List<String> mPlayerUrls;
    private boolean isAddcategoryUrl;

    private Dilidili() {
    }

    public static Dilidili getInstance() {
        return new Dilidili();
    }

    @Override
    public Observable<Map<String, List<Bangumi>>> getHomePageBangumiData() {
        return Observable.fromArray(BASE_URL)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    AtomicReference<Observable> returnObservable = new AtomicReference<>();
                    Map<String, List<Bangumi>> homeGroups = new LinkedHashMap<>();
                    //解析轮播图
                    Elements bannerElements = document.getElementsByClass("swiper-slide");
                    Observable.fromIterable(bannerElements)
                            .filter(element -> !element.attr("href").contains(".html"))//过滤广告
                            .map(element -> {
                                String id = parseId(element.attr("href"));
                                String cover = element.child(0).attr("src");
                                String name = element.child(0).attr("alt");
                                Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                                return bangumi;
                            })
                            .toList()
                            .subscribe(
                                    bangumis -> homeGroups.put(BannerHomeAdapter.BANNER_KEY, bangumis),
                                    throwable -> returnObservable.set(Observable.error(throwable)));

                    //解析更新
                    if (returnObservable.get() == null) {
                        Observable.fromIterable(document.getElementById("newId").child(0).children())
                                .take(6)
                                .map(element -> {
                                    String url = element.getElementsByTag("a").attr("href");
                                    Document doc = Jsoup.parse(HttpRequestUtil.getResponseBodyString(url));
                                    String id = parseId(doc.getElementsByTag("h4").get(0).child(0).attr("href"));
                                    String cover = element.getElementsByClass("coverImg").attr("style");
                                    cover = cover.substring(cover.indexOf("(") + 1, cover.lastIndexOf(")"));
                                    String name = element.getElementsByTag("h3").text();
                                    String jiTotal = element.getElementsByTag("h4").text();
                                    Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                                    bangumi.setRemarks(jiTotal);

                                    return bangumi;
                                })
                                .toList()
                                .doOnError(throwable -> LogUtil.i(throwable.toString() + "dili home"))
                                .subscribe(
                                        bangumis -> homeGroups.put("最近更新", bangumis),
                                        throwable -> returnObservable.set(Observable.error(throwable)));
                    }

                    //解析本季新番内容
                    if (returnObservable.get() == null) {
                        String newBgmUrl = BASE_URL_PC + document.getElementById("navbar").child(0).child(1).child(0).attr("href");
                        Observable.just(newBgmUrl)
                                .compose(ParseUtil.html2Transformer())
                                .flatMap(newBgmDocument -> Observable.fromIterable(newBgmDocument.getElementsByClass("anime_list").get(0).children()))
                                .take(10)
                                .map(element -> {
                                    String id = parseId(element.getElementsByTag("h3").get(0).child(0).attr("href"));
                                    String cover = element.getElementsByTag("img").attr("src");
                                    String name = element.getElementsByTag("h3").get(0).child(0).text();
                                    Elements pElements = element.getElementsByTag("p");
                                    String intro = pElements.get(pElements.size() - 2).text();
                                    String type = parsetype(element.getElementsByClass("d_label").get(2));
                                    Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                                    BangumiInfo info = new BangumiInfo(null, null, null, type, intro, null);
                                    bangumi.setBangumiInfo(info);
                                    return bangumi;
                                })
                                .toList()
                                .subscribe(
                                        bangumis -> homeGroups.put("本季新番", bangumis),
                                        throwable -> returnObservable.set(Observable.error(throwable)));
                    }

                    //解析推荐
                    if (returnObservable.get() == null) {
                        final String[] key = new String[1];
                        Observable.just(BASE_URL_PC + "/bufantuijian.html")
                                .compose(ParseUtil.html2Transformer())
                                .flatMap(tuijianDocument -> {
                                    key[0] = tuijianDocument.getElementsByClass("web-font").text();
                                    return Observable.fromIterable(tuijianDocument.getElementsByClass("media"));
                                })
                                .map(element -> {
                                    Element leftElement = element.getElementsByClass("media-left").get(0);
                                    String id = parseId(leftElement.child(0).attr("href"));
                                    String cover = leftElement.getElementsByClass("media-object").attr("src");
                                    Element bodyElement = element.getElementsByClass("media-body").get(0);
                                    String name = bodyElement.getElementsByClass("media-heading").text();
                                    Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                                    String intro = element.getElementsByTag("p").get(0).text();
                                    String type = parsetype(element);
                                    BangumiInfo bangumiInfo = new BangumiInfo("", "", "", type, intro, "");
                                    bangumi.setBangumiInfo(bangumiInfo);
                                    return bangumi;
                                })
                                .doOnError(throwable -> LogUtil.i(throwable.toString()))
                                .toList()
                                .subscribe(
                                        bangumis -> homeGroups.put(key[0], bangumis),
                                        throwable -> returnObservable.set(Observable.error(throwable)));
                    }

                    return returnObservable.get() == null ? Observable.just(homeGroups) : returnObservable.get();
                })
                .subscribeOn(Schedulers.io());
    }

    private Bangumi parshCategoryBangumi(Element element) {
        String id = parseId(element.child(0).attr("href"));
        Element contentElement = element.child(0);
        String cover = contentElement.getElementsByClass("episodeImg").attr("style");
        cover = cover.substring(cover.indexOf("(") + 1);
        String name = contentElement.getElementsByClass("ac").text();
        Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
        return bangumi;
    }

    private String parsetype(Element bodyElement) {
        Elements elements = bodyElement.getElementsByTag("a");
        StringBuilder builder = new StringBuilder();
        for (Element element : elements) {
            if (element.hasAttr("target")) {
                continue;
            }
            builder.append(element.text());
            builder.append(" ");
        }
        return builder.toString();
    }

    private String parseId(String idUrl) {
        String[] strings = idUrl.trim().split("/");
        String id = strings[strings.length - 1];
        return id;
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


    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        return Observable.just(BASE_URL_PC + "/anime/" + id)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Element contentElement = document.getElementsByClass("detail con24 clear").get(0);
                    Elements labels = contentElement.getElementsByClass("d_label");
                    String niandia = labels.get(1).getElementsByTag("a").text();
                    String type = parsetype(labels.get(2));
                    String jiTotal = labels.get(3).text();
                    String staff = null;
                    if (labels.size() == 5) {
                        staff = labels.get(4).text();
                    }
                    labels = contentElement.getElementsByClass("d_label2");
                    String cast = parsetype(labels.get(1));
                    cast = cast.replaceAll(" ", "\n");
                    String intor = labels.get(2).text();
                    BangumiInfo info = new BangumiInfo(niandia, cast, staff, type, intor, jiTotal);
                    return info;
                })
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "    info"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        if (mPlayerUrls == null) {
            mPlayerUrls = new ArrayList<>();
        }
        return Observable.just(BASE_URL + "/anime/" + id)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    Elements elements = document.getElementsByClass("episode");
                    return Observable.fromIterable(elements.get(0).child(1).children())
                            .map(element -> {
                                String playUrl = element.child(0).attr("href");
                                String jiName = "第" + element.child(0).text() + "集";
                                mPlayerUrls.add(playUrl);
                                TextItemSelectBean selectBean = new TextItemSelectBean(jiName);
                                return selectBean;
                            })
                            .toList()
                            .toObservable();
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        if (mPlayerUrls == null || mPlayerUrls.isEmpty()) {
            return Observable.error(new IllegalAccessError("没有解析集"));
        }
        return Observable.just(mPlayerUrls.get(ji))
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    String[] htmlUrl = document.getElementsByTag("iframe").attr("src").split("=");
                    String url = htmlUrl[htmlUrl.length - 1];
                    VideoUrl videoUrl = new VideoUrl(url);
                    if (url.contains(".html")) {
                        videoUrl.setUrl(BASE_URL + "/anime/" + id);
                        videoUrl.setHtml(true);
                    }
                    return videoUrl;
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
        return Observable.just(BASE_URL_PC + "/anime/" + id)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("m_pic clear").get(0).children()))
                .map(element -> {
                    String recommendBangumiId = parseId(element.child(0).attr("href"));
                    String cover = element.getElementsByTag("img").attr("src");
                    String name = element.getElementsByTag("p").text();
                    Bangumi bangumi = new Bangumi(recommendBangumiId, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                    return bangumi;
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());

    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        if ("最近更新".equals(category)) {
            return Observable.just(BASE_URL_PC + "/zxgx/")
                    .compose(ParseUtil.html2Transformer())
                    .flatMap(document -> Observable.fromIterable(document.getElementsByClass("book article").get(0).children()))
                    .map(element -> {
                        Document document = Jsoup.parse(HttpRequestUtil.getResponseBodyString(element.attr("href")));
                        String id = parseId(document.getElementsByTag("h2").get(0).child(1).attr("href"));
                        String name = element.getElementsByTag("p").get(0).text();
                        String jiTotal = element.getElementsByTag("p").get(1).text();
                        String cover = element.getElementsByTag("div").attr("style");
                        cover = cover.substring(cover.indexOf("(") + 1, cover.lastIndexOf(")"));
                        Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                        bangumi.setRemarks(jiTotal);
                        return bangumi;
                    })
                    .toList()
                    .toObservable()
                    .subscribeOn(Schedulers.io());
        } else if (category.contains("新番")) {
            return Observable.just(BASE_URL)
                    .compose(ParseUtil.html2Transformer())
                    .map(document -> BASE_URL + document.getElementById("navbar").child(0).child(1).child(0).attr("href"))
                    .compose(ParseUtil.html2Transformer())
                    .flatMap(newBgmDocument -> Observable.fromIterable(newBgmDocument.getElementById("episode_list").children()))
                    .map(this::parshCategoryBangumi)
                    .toList()
                    .toObservable()
                    .subscribeOn(Schedulers.io());
        } else {
            LogUtil.i("dsfasdf");
            return Observable.just(BASE_URL + "/fenlei.html")
                    .compose(ParseUtil.html2Transformer())
                    .flatMap(document -> Observable.fromIterable(document.getElementsByClass("w").get(1).child(0).children()))
                    .filter(categorElement -> categorElement.getElementsByTag("p").text().contains(category))
                    .map(categorElement -> categorElement.child(0).attr("href"))
                    .compose(ParseUtil.html2Transformer())
                    .flatMap(document -> Observable.fromIterable(document.getElementById("episode_list").children()))
                    .map(this::parshCategoryBangumi)
                    .toList()
                    .doOnError(throwable -> LogUtil.i(throwable.toString() + "  dilica"))
                    .toObservable()
                    .subscribeOn(Schedulers.io());
        }
    }

    @Override
    public Observable<Results> getNextCategoryBangumis() {
        return Observable.just(new Results(true, null));
    }


    @Override
    public Observable<List<CategorItem>> getCategorItems() {
        return Observable.just(BASE_URL + "/fenlei.html")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("w").get(1).child(0).children()))
                .map(categorElement -> {
                    String img = categorElement.getElementsByTag("img").attr("src");
                    String categorName = categorElement.getElementsByTag("p").text();
                    CategorItem categorItem = new CategorItem(img, categorName);
                    return categorItem;
                })
                .toList()
                .toObservable()
                .doOnNext(categorItems -> isAddcategoryUrl = true)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<List<Bangumi>>> getBangumiTimeTable() {
        return Observable.just("http://www.dilidili.name")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("wrp animate").get(1).children()))
                .flatMap(dayElement -> {
                    return Observable.fromIterable(dayElement.child(0).children())
                            .map(contentElement -> {
                                String id = parseId(contentElement.attr("href"));
                                String cover = contentElement.getElementsByTag("img").attr("src");
                                String name = contentElement.getElementsByTag("img").attr("alt");
                                Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.DILIDLI, name, cover);
                                if (contentElement.getElementsByTag("p").size() == 2) {
                                    bangumi.setRemarks(contentElement.getElementsByTag("p").get(1).text());
                                }
                                return bangumi;
                            })
                            .toList()
                            .toObservable();
                })
                .toList()

                .toObservable()
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
