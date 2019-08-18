package com.TyxApp.bangumi.data.source.remote;

import android.text.TextUtils;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;

public class Silisili implements IBangumiParser {
    private static final String BASE_URL_PHONE = "http://m.silisili.me";
    private static final String BASE_URL_PC = "http://www.silisili.me";
    private String nextSearchUrl;
    private List<String> mPlayerUrls;
    private String nextCategorUrl;

    public static Silisili newInstance() {
        return new Silisili();
    }

    @Override
    public Observable<Map<String, List<Bangumi>>> getHomePageBangumiData() {
        return Observable.just(BASE_URL_PC)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Map<String, List<Bangumi>> homeGroups = new LinkedHashMap<>();
                    //轮播图
                    List<Bangumi> banners = new ArrayList<>();
                    for (Element child : document.getElementsByClass("swiper-wrapper").get(0).children()) {
                        Element aElement = child.getElementsByTag("a").get(0);
                        if (!aElement.attr("href").contains(".html")) {
                            continue;
                        }
                        String id = parseId(aElement.attr("href"));
                        String name = aElement.attr("title");
                        String img = aElement.getElementsByTag("img").attr("src");
                        Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.SILISILI, name, img);
                        bangumi.setImg(img);
                        banners.add(bangumi);
                    }
                    homeGroups.put(BannerHomeAdapter.BANNER_KEY, banners);

                    //主页番剧
                    Elements HomeElements = document.getElementsByClass("con24 m-20 fl");

                    for (Element element : HomeElements) {
                        List<Bangumi> bangumis = new ArrayList<>();
                        String title = element.getElementsByTag("h2").text();
                        for (Element bangumiElement : element.getElementsByClass("m_pic clear").get(0).children()) {
                            String id = parseId(bangumiElement.child(0).attr("href"));
                            String name = bangumiElement.child(0).attr("title");
                            String cover = bangumiElement.getElementsByTag("img").attr("src");
                            if (!cover.contains("http")) {
                                cover = BASE_URL_PHONE + cover;
                            }
                            Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.SILISILI, name, cover);
                            bangumis.add(bangumi);
                        }
                        homeGroups.put(title, bangumis);
                    }
                    return homeGroups;
                })
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  silihome"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        nextSearchUrl = null;
        return Observable.just(word)
                .map(s -> new FormBody.Builder()
                        .add("show", "title,ftitle,zz")
                        .add("tbname", "movie")
                        .add("tempid", "1")
                        .add("keyboard", s)
                        .build())
                .map(formBody -> HttpRequestUtil.postFromResult(BASE_URL_PHONE + "/e/search/index.php", formBody))
                .map(Jsoup::parse)
                .map(document -> {
                    Elements elements = document.getElementsByClass("pages");
                    if (elements.size() > 0) {
                        Element pagesElement = elements.get(0);
                        if (pagesElement.toString().contains("下一页")) {
                            Elements pages = pagesElement.child(0).children();
                            nextSearchUrl = BASE_URL_PHONE + pages.get(pages.size() - 2).child(0).attr("href");
                        }
                    }
                    return document;
                })
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("search_result")))
                .compose(parseSearchBangumi())
                .toList()
                .toObservable()
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  sili search"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Results> nextSearchResult() {
        if (TextUtils.isEmpty(nextSearchUrl)) {
            return Observable.empty();
        }
        return Observable.just(nextSearchUrl)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Element pagesElement = document.getElementsByClass("pages").get(0);
                    if (pagesElement.toString().contains("下一页")) {
                        Elements pages = pagesElement.child(0).children();
                        nextSearchUrl = BASE_URL_PHONE + pages.get(pages.size() - 2).child(0).attr("href");
                    } else {
                        nextSearchUrl = null;
                    }
                    return document;
                })
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("search_result")))
                .compose(parseSearchBangumi())
                .toList()
                .map(bangumis -> new Results(false, bangumis))
                .toObservable()
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  sili nextsearch"))
                .subscribeOn(Schedulers.io());
    }

    private ObservableTransformer<Element, Bangumi> parseSearchBangumi() {
        return observable -> observable.map(element -> {
            String id = parseId(element.child(0).attr("href"));
            String cover = element.getElementsByTag("img").attr("src");
            if (!cover.contains("http")) {
                cover = BASE_URL_PHONE + cover;
            }
            String name = element.getElementsByTag("h2").text();
            return new Bangumi(id, BangumiPresistenceContract.BangumiSource.SILISILI, name, cover);
        });
    }

    private String parseId(String id) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(id);
        return matcher.replaceAll("").trim();
    }

    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        return Observable.just(BASE_URL_PC + "/anime/"+ id +".html")
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Elements d_labelElements = document.getElementsByClass("d_label");
                    String niandai = d_labelElements.get(1).child(1).text();
                    String type = d_labelElements.get(2).text();
                    String jiTotal = d_labelElements.get(3).text();
                    String intro = document.getElementsByClass("d_label2").get(1).text();
                    return new BangumiInfo(niandai, "暂无信息", "暂无信息", type, intro, jiTotal);
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        if (mPlayerUrls == null) {
            mPlayerUrls = new ArrayList<>();
        } else {
            mPlayerUrls.clear();
        }
        return Observable.just(BASE_URL_PHONE + "/e/action/play_list.php?id=" + id)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementById("hua_ul1").children()))
                .map(element -> {
                    String name = element.child(0).text();
                    mPlayerUrls.add(BASE_URL_PHONE + element.child(0).attr("href"));
                    return new TextItemSelectBean(name);
                })
                .toList()
                .toObservable()
                .doOnNext(Collections::reverse)
                .doOnComplete(() -> Collections.reverse(mPlayerUrls))
                .doOnError(throwable -> LogUtil.i(throwable.toString() + "  sili jilist"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        return Observable.just(mPlayerUrls.get(ji))
                .compose(ParseUtil.html2Transformer())
                .map(document -> document.getElementsByTag("iframe").get(0).attr("src"))
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Elements elements = document.getElementsByTag("source");
                    VideoUrl videoUrl = new VideoUrl();
                    if (elements.size() == 0) {
                        videoUrl.setUrl(BASE_URL_PHONE + "/play/" + id + "-" + ji + 1 + ".html");
                        videoUrl.setHtml(true);
                    } else {
                        videoUrl.setUrl(elements.attr("src"));
                    }
                    return videoUrl;
                })
                .subscribeOn(Schedulers.io());

    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
        return Observable.just(BASE_URL_PHONE + "/play/" + id + "-1.html")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("plist02").get(0).children()))
                .map(element -> {
                    String bangumiId = element.child(0).attr("href");
                    Pattern pattern = Pattern.compile("[^0-9]");
                    Matcher matcher = pattern.matcher(bangumiId);
                    bangumiId = matcher.replaceAll("").trim();
                    String cover = element.getElementsByTag("img").attr("src");
                    if (!cover.contains("http")) {
                        cover = BASE_URL_PHONE + cover;
                    }
                    String name = element.getElementsByTag("p").text();
                    return new Bangumi(bangumiId, BangumiPresistenceContract.BangumiSource.SILISILI, name, cover);
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        nextCategorUrl = null;
        return Observable.just(category)
                .map(URLEncoder::encode)
                .map(encodeWord -> BASE_URL_PHONE + "/tag/" + encodeWord + "-0-0-0-0.html")
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Element pagesElement = document.getElementsByClass("pages").get(0);
                    if (pagesElement.toString().contains("下一页")) {
                        int pageCount = pagesElement.child(0).childNodeSize();
                        nextCategorUrl = BASE_URL_PHONE + pagesElement.child(0).child(pageCount - 1).getElementsByTag("a").attr("href");
                    }
                    return document;
                })
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("plist02").get(0).children()))
                .compose(parseCategoryBangumis())
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Results> getNextCategoryBangumis() {
        if (TextUtils.isEmpty(nextCategorUrl)) {
            return Observable.empty();
        }
        return Observable.just(nextCategorUrl)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Element pagesElement = document.getElementsByClass("pages").get(0);
                    if (pagesElement.toString().contains("下一页")) {
                        int pageCount = pagesElement.child(0).childNodeSize();
                        nextCategorUrl = BASE_URL_PHONE + pagesElement.child(0).child(pageCount - 1).getElementsByTag("a").attr("href");
                    } else {
                        nextCategorUrl = null;
                    }
                    return document;
                })
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("plist02").get(0).children()))
                .compose(parseCategoryBangumis())
                .toList()
                .map(bangumis -> new Results(false, bangumis))
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    private ObservableTransformer<Element, Bangumi> parseCategoryBangumis() {
        return observable -> observable.map(element -> {
            String id = parseId(element.getElementsByTag("a").attr("href"));
            String cover = element.getElementsByTag("img").attr("src");
            if (!cover.contains("http")) {
                cover = BASE_URL_PHONE + cover;
            }
            String name = element.getElementsByTag("p").text();
            return new Bangumi(id, BangumiPresistenceContract.BangumiSource.SILISILI, name, cover);
        });
    }

    @Override
    public Observable<List<CategorItem>> getCategorItems() {
        return Observable.just(BASE_URL_PHONE + "/dm")
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("plist01").get(0).children()))
                .map(element -> {
                    String name = element.getElementsByTag("p").text();
                    String cover = BASE_URL_PHONE + element.getElementsByTag("img").attr("src");
                    return new CategorItem(cover, name);
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<List<Bangumi>>> getBangumiTimeTable() {
        return Observable.just(BASE_URL_PC)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("time_con")))
                .map(element -> {
                    List<Bangumi> bangumis = new ArrayList<>();
                    for (Element bangumiElement : element.getElementsByClass("clear").get(0).children()) {
                        String id = parseId(bangumiElement.getElementsByTag("a").attr("href"));
                        String name = bangumiElement.getElementsByTag("a").attr("title");
                        String cover = bangumiElement.getElementsByTag("img").attr("src");
                        String ji = bangumiElement.getElementsByTag("i").text();
                        Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.SILISILI, name, cover);
                        bangumi.setRemarks(ji);
                        bangumis.add(bangumi);
                    }
                    return bangumis;
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }
}
