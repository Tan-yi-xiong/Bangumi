package com.TyxApp.bangumi.data.source.remote;

import android.util.Base64;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Result;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.ParseUtil;
import com.google.gson.JsonParser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class Qimiqimi implements IBangumiParser {
    private static final String BASE_URL = "http://www.qimiqimi.co";
    private int page;
    private String searchWord;
    private List<TextItemSelectBean> jiList;
    private List<String> htmlPlayerUrls;
    private List<Bangumi> recommendBangumis;

    @Override
    public Observable<Map<String, List<Bangumi>>> getHomePageBangumiData() {
        return null;
    }

    public static Qimiqimi newInstance() {
        return new Qimiqimi();
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        page = 1;
        searchWord = word;
        return Observable.just(word)
                .map(URLEncoder::encode)
                .map(encodeWord -> BASE_URL + "/index.php/vod/search/wd/" + encodeWord + ".html")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("show-list").get(0).children()))
                .compose(parseSearchElement())
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    private String parseId(String idUrl) {
        return idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.lastIndexOf("."));
    }

    private ObservableTransformer<Element, Bangumi> parseSearchElement() {
        return observable -> observable.map(element -> {
            String id = parseId(element.getElementsByTag("a").attr("href"));
            String cover = BASE_URL + element.getElementsByTag("img").attr("src");
            String name = element.getElementsByTag("img").attr("alt");
            String ji = element.getElementsByClass("color").get(0).text();
            Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.QIMIQIMI, name, cover);
            bangumi.setRemarks(ji);
            return bangumi;
        });
    }

    @Override
    public Observable<Result<List<Bangumi>>> nextSearchResult() {
        page++;
        return Observable.just(searchWord)
                .map(URLEncoder::encode)
                .map(encodeWord -> BASE_URL + "/index.php/vod/search/wd/" + encodeWord + "/page/" + page + ".html")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    Elements bangumiElements = document.getElementsByClass("show-list").get(0).children();
                    if (bangumiElements.size() == 0) {
                        return Observable.just(new Result<List<Bangumi>>(true, null));
                    } else {
                        return Observable.fromIterable(bangumiElements)
                                .compose(parseSearchElement())
                                .toList()
                                .map(bangumis -> new Result<>(false, bangumis))
                                .toObservable()
                                .doOnError(throwable -> LogUtil.i(throwable.toString() + " qimi nextsearch"));

                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        if (jiList == null) {
            jiList = new ArrayList<>();
        } else {
            jiList.clear();
        }
        return Observable.just(BASE_URL + "/index.php/detail/" + id + ".html")
                .compose(ParseUtil.html2Transformer())
                .map(document -> {//解析集数
                    try {
                        Elements elements = document.getElementsByClass("ui-box marg").get(0).getElementsByClass("video_list fn-clear");
                        if (elements.size() > 0) {
                            htmlPlayerUrls = new ArrayList<>();
                            for (Element child : elements.get(0).children()) {
                                TextItemSelectBean selectBean = new TextItemSelectBean(child.text());
                                jiList.add(selectBean);
                                htmlPlayerUrls.add(BASE_URL + child.attr("href"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return document;
                })
                .map(document -> {//解析推荐
                    try {
                        if (htmlPlayerUrls.size() > 0) {
                            if (recommendBangumis == null) {
                                recommendBangumis = new ArrayList<>();
                            } else {
                                recommendBangumis.clear();
                            }
                            Element recommendElement = document.getElementsByClass("img-list dis").get(0);
                            for (Element child : recommendElement.children()) {
                                String name = child.getElementsByTag("h2").text();
                                String bangumiId = parseId(child.getElementsByTag("a").attr("href"));
                                String cover = BASE_URL + child.getElementsByTag("img").attr("src");
                                recommendBangumis.add(new Bangumi(bangumiId, BangumiPresistenceContract.BangumiSource.QIMIQIMI, name, cover));
                            }
                        }
                    } catch (Exception e) {
                       LogUtil.i(e.toString());
                    }
                    return document;
                })
                .map(document -> {
                    Elements castElements = document.getElementsByClass("nyzhuy").get(0).getElementsByTag("a");
                    StringBuilder builder = new StringBuilder();
                    for (Element castElement : castElements) {
                        builder.append(castElement.text());
                        builder.append(" ");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    String cast = builder.toString();

                    Elements leixingElements = document.getElementsByClass("fn-left").get(5).getElementsByTag("a");
                    builder.delete(0, builder.length() - 1);
                    for (Element leixingElement : leixingElements) {
                        builder.append(leixingElement.text());
                        builder.append(" ");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    String leixing = builder.toString();

                    String staff = document.getElementsByClass("fn-right").get(0).getElementsByTag("a").text();
                    String naindai = document.getElementById("addtime").text();
                    String intro = document.getElementsByClass("juqing").get(0).getElementsByTag("dd").text();
                    return new BangumiInfo(naindai, cast, staff, leixing, intro, "");
                })
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  info"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        return Observable.just(jiList);
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        return Observable.just(htmlPlayerUrls.get(ji))
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    String jsonData = document.getElementById("bofang_box").child(0).toString();
                    jsonData = jsonData.substring(jsonData.indexOf("{"), jsonData.lastIndexOf("}") + 1);
                    String url = new JsonParser().parse(jsonData).getAsJsonObject().get("url").getAsString();
                    url = URLDecoder.decode(new String(Base64.decode(url, Base64.DEFAULT)), "utf-8");
                    return new VideoUrl(url);
                })
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  qiliqi url "))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
        return recommendBangumis == null ? Observable.error(new IllegalAccessError()) : Observable.just(recommendBangumis);
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        return null;
    }

    @Override
    public Observable<Result<List<Bangumi>>> getNextCategoryBangumis() {
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

    @Override
    public Observable<Result<BaseDanmakuParser>> getDanmakuParser(String id, int ji) {
        return Observable.just(new Result<>(true, null));
    }
}
