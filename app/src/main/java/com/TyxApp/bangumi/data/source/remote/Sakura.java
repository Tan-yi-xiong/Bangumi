package com.TyxApp.bangumi.data.source.remote;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Result;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.ParseUtil;

import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class Sakura implements IBangumiParser {
    private static String baseUrl = "http://m.imomoe.io";
    private String nextSearchPageUrl;
    private List<String> mVideoPlayerUrls;

    public static Sakura getInstance() {
        return new Sakura();
    }


    @Override
    public Observable<Map<String, List<Bangumi>>> getHomePageBangumiData() {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        String searchUrl = null;
        try {
            searchUrl = baseUrl + "/search.asp?searchword=" + URLEncoder.encode(word, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Observable.just(searchUrl).compose(searchLogicObservable());
    }

    private ObservableTransformer<String, List<Bangumi>> searchLogicObservable() {
        return observable -> observable.compose(ParseUtil.html2Transformer("GB2312"))
                .map(document -> {
                    Element element = document.getElementsByClass("am-pagination-next").get(0).child(0);
                    if (element.hasAttr("href")) {
                        nextSearchPageUrl = baseUrl + "/search.asp" + element.attr("href");
                    } else {
                        nextSearchPageUrl = null;
                    }
                    return document;
                })
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("am-gallery-item")))
                .map(this::parshBangumi)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    private Bangumi parshBangumi(Element element) {
        String url = element.getElementsByTag("a").attr("href");
        Pattern pattern = Pattern.compile("[^0-9]");
        String id = pattern.matcher(url).replaceAll("").trim();

        String cover = element.getElementsByTag("img").attr("data-original");
        String name = element.getElementsByTag("img").attr("alt");

        String[] texts = element.getElementsByClass("am-gallery-desc").text().split(" ");
        String total = texts[texts.length - 1];
        Bangumi bangumi = new Bangumi(id, BangumiPresistenceContract.BangumiSource.SAKURA, name, cover);
        bangumi.setRemarks(total);
        bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.SAKURA);
        return bangumi;
    }

    @Override
    public Observable<Result<List<Bangumi>>> nextSearchResult() {
        Observable<Result<List<Bangumi>>> observable = null;
        if (nextSearchPageUrl == null) {
            observable = Observable.just(new Result<>(true, null));//已经没有更多了
        } else {
            observable = Observable.just(nextSearchPageUrl)
                    .compose(searchLogicObservable())
                    .map(bangumis -> new Result<>(false, bangumis));
        }
        return observable;
    }

    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        return Observable.just(baseUrl + "/view/" + id +".html")
                .compose(ParseUtil.html2Transformer("GB2312"))
                .map(document -> {
                    Element element = document.getElementById("p-info");
                    String type = parseChilds(element.child(1));
                    String niandai = element.child(3).text();
                    String jiTotal = element.child(5).text();
                    String intro = document.getElementsByClass("txtDesc autoHeight").text();
                    return new BangumiInfo(niandai, "暂无信息", "暂无信息", type, intro, jiTotal);
                })
                .subscribeOn(Schedulers.io());
    }

    private String parseChilds(Element element) {
        StringBuilder builder = new StringBuilder();
        for (Element child : element.getElementsByTag("a")) {
            builder.append(child.text());
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        String url = baseUrl + "/player/"+ id +"-0-0.html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    String dataUrl = document.getElementsByClass("player").get(0).child(0).attr("src");
                    dataUrl = baseUrl + dataUrl;
                    return parseJi(id, HttpRequestUtil.getResponseBodyString(dataUrl));
                })
                .subscribeOn(Schedulers.io());
    }

    private List<TextItemSelectBean> parseJi(String id, String str) throws Exception {
        List<TextItemSelectBean> jiList = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>();
        str = str.substring(str.indexOf('['), str.lastIndexOf(']') - 1);
        String[] xianlu = str.split("],");
        for (String jidata : xianlu) {
            if (!jiList.isEmpty()) break;
            if (!jidata.contains("hd_iask") && !jidata.contains("http")) continue;
            jidata = jidata.substring(jidata.lastIndexOf('[') + 1, jidata.lastIndexOf(']'));
            String[] jilist = jidata.split("','");
            for (String ji : jilist) {
                if (ji.contains("'")) {
                    ji = ji.replace("'", "");
                }
                String[] jiSplit = ji.split("\\$");

                if ("hd_iask".equals(jiSplit[2]) || (jiSplit[1].contains("http") && !jiSplit[1].contains(".html"))) {
                    TextItemSelectBean itemSelectBean = new TextItemSelectBean(ParseUtil.unicodeToString(jiSplit[0]));
                    jiList.add(itemSelectBean);
                    videoUrls.add(jiSplit[1]);
                }
            }
        }
        if (!videoUrls.isEmpty()) {
            mVideoPlayerUrls = videoUrls;
        }
        return jiList;
    }

    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        if (mVideoPlayerUrls == null || mVideoPlayerUrls.isEmpty()) {
            VideoUrl videoUrl = new VideoUrl(baseUrl + "/player/" + id + "-0-"+ ji +".html");
            videoUrl.setHtml(true);
            return Observable.just(videoUrl);
        } else {
            return Observable.just(new VideoUrl(mVideoPlayerUrls.get(ji)));
        }
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
        String url = baseUrl + "/player/" + id + "-0-0.html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer("GB2312"))
                .flatMap(document -> Observable.fromIterable(document.getElementsByClass("am-gallery-item")))
                .map(this::parshBangumi)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io());
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
