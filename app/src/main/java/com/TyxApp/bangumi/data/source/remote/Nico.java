package com.TyxApp.bangumi.data.source.remote;

import android.os.Build;
import android.util.SparseArray;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.ParseUtil;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.RequiresApi;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class Nico implements BaseBangumiParser {
    private String baseUrl = "http://www.nicotv.me";
    private SparseArray<List<String>> jiUrlsCollect;
    private SparseArray<SparseArray<VideoUrl>> videoUrlsCollect;
    private List<String> searchMoreHtml;
    private static Nico INSTANCE;
    private static AtomicInteger INSTANCECOUNTER = new AtomicInteger();

    private Nico() {
        videoUrlsCollect = new SparseArray<>();
        jiUrlsCollect = new SparseArray<>();
        searchMoreHtml = new ArrayList<>();
    }

    public static Nico getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nico();
        }
        INSTANCECOUNTER.getAndIncrement();
        return INSTANCE;
    }

    @Override
    public void onDestroy() {
        jiUrlsCollect.clear();
        videoUrlsCollect.clear();
        if (INSTANCECOUNTER.getAndDecrement() == 1) {
            INSTANCE = null;
        }
    }

    @Override
    public Observable<List<Bangumi>> getHomePageBangumiData() {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        searchMoreHtml.clear();
        String url = baseUrl + "/video/search/" + word + ".html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    saveSearchResultUrls(document);
                    return Observable.create((ObservableOnSubscribe<List<Bangumi>>) emitter -> emitter.onNext(parseSearchPageBangumis(document)));
                })
                .subscribeOn(Schedulers.io());

    }

    private void saveSearchResultUrls(Document document) {
        Elements elements = document.getElementsByClass("pagination pagination-lg hidden-xs");
        if (!elements.isEmpty()) {
            Element element = elements.get(0);
            Elements childrens = element.children();
            for (int i = 1; i < childrens.size(); i++) {
                String resultUrl = baseUrl + childrens.get(i).getElementsByTag("a").attr("href");
                searchMoreHtml.add(resultUrl);
            }
        }
    }

    private List<Bangumi> parseSearchPageBangumis(Document document) {
        Element list = document.getElementsByClass("list-unstyled vod-item-img ff-img-215").get(0);
        Elements dataNode = list.getElementsByClass("image");
        List<Bangumi> bangumis = new ArrayList<>();
        for (Element data : dataNode) {
            bangumis.add(parseBangumi(data));
        }
        return bangumis;
    }

    private Bangumi parseBangumi(Element element) throws NumberFormatException {
        String id = element.getElementsByTag("a").attr("href");
        id = id.substring(id.lastIndexOf("/") + 1, id.lastIndexOf("."));
        String cover = element.getElementsByTag("img").attr("data-original");
        String name = element.getElementsByTag("img").attr("alt");
        String remark = element.getElementsByTag("span").text();
        Bangumi bangumi = new Bangumi();
        bangumi.setVideoSoure(BangumiPresistenceContract.BangumiSource.NiICO);
        bangumi.setName(name);
        bangumi.setRemarks(remark);
        bangumi.setCover(cover);
        bangumi.setVodId(Integer.valueOf(id));
        return bangumi;
    }

    @Override
    public Observable<List<Bangumi>> nextSearchResult() {
        if (searchMoreHtml.isEmpty()) {
            return Observable.empty();
        }
        String nextUrl = searchMoreHtml.remove(0);
        return Observable.just(nextUrl)
                .compose(ParseUtil.html2Transformer())
                .flatMap(htmlData -> Observable.create((ObservableOnSubscribe<List<Bangumi>>) emitter -> emitter.onNext(parseSearchPageBangumis(htmlData))))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<String> getIntor(int id) {
        return Observable.just("暂无简介");
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(int id) {
        String url = baseUrl + "/video/play/" + id + "-1-1.html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .concatMapIterable(document -> document.getElementsByAttributeValue("data-more", "36"))
                .filter(element -> !element.toString().contains("提取码"))
                .take(1)
                .map(jiListElement -> {
                    List<TextItemSelectBean> selectBeans = new ArrayList<>();
                    List<String> jiUrls = new ArrayList<>();
                    for (Element child : jiListElement.children()) {
                        String text = child.getElementsByTag("a").text();
                        String jiUrl = baseUrl + child.getElementsByTag("a").attr("href");
                        TextItemSelectBean bean = new TextItemSelectBean(text);
                        selectBeans.add(bean);
                        jiUrls.add(jiUrl);
                    }
                    jiUrlsCollect.append(id, jiUrls);
                    return selectBeans;
                })
                .subscribeOn(Schedulers.io());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Observable<VideoUrl> getplayerUrl(int id, int ji) {
        SparseArray<VideoUrl> videoUrls = videoUrlsCollect.get(id);
        if (videoUrls != null) {
            if (videoUrls.get(ji) != null) {
                return Observable.just(videoUrls.get(ji));
            }
        }
        String url = jiUrlsCollect.get(id).get(ji);
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    Element element = document.getElementById("cms_player").child(0);
                    String videoHtmlUrl = baseUrl + element.attr("src");
                    String html = HttpRequestUtil.getGetRequestResponseBodyString(videoHtmlUrl);
                    return Observable.just(html);
                })
                .map(html -> {
                    html = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
                    NicoPlayerUrlBean bean = new Gson().fromJson(html, NicoPlayerUrlBean.class);
                    return parsePlayerUrl(id, ji, bean);
                })
                .subscribeOn(Schedulers.io());

    }

    private VideoUrl parsePlayerUrl(int id, int ji, NicoPlayerUrlBean bean) throws IOException {
        String url;
        boolean isHtmlUrl = false;
        if ("360biaofan".equals(bean.name)) {
            url = bean.url + "&time=" + bean.time + "&auth_key=" + bean.auth_key;
            url = parseVideoUrlFormhtml(url);
        } else if (bean.url.contains(".mp4")) {
            url = bean.url.substring(bean.url.indexOf("=") + 1);
        } else if (bean.url.contains("tyjx2.kingsnug.cn") && bean.name.equals("haokan_baidu")) {
            url = bean.url + "&time=" + bean.time + "&auth_key=" + bean.auth_key;
            url = parseVideoUrlFormhtml(url);
        } else if ("kkm3u8".equals(bean.name)) {
            url = bean.url;
        } else {
            url = jiUrlsCollect.get(id).get(ji);
            isHtmlUrl = true;
        }
        VideoUrl videoUrl = new VideoUrl(url);
        videoUrl.setHtml(isHtmlUrl);
        SparseArray<VideoUrl> videoUrls = videoUrlsCollect.get(id);
        if (videoUrls == null) {
            videoUrls = new SparseArray<>();
            videoUrlsCollect.append(id, videoUrls);
        }
        videoUrls.append(ji, videoUrl);
        return videoUrl;
    }

    private String parseVideoUrlFormhtml(String url) throws IOException {
        String data = HttpRequestUtil.getGetRequestResponseBodyString(url);
        Document d = Jsoup.parse(data);
        Element e = d.getElementsByTag("script").get(1);
        data = e.toString();
        data = data.substring(data.lastIndexOf("{"), data.lastIndexOf(","));
        url = data.substring(data.indexOf("\"") + 1, data.lastIndexOf("\""));
        return url;
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(int id) {
        String url = baseUrl + "/video/detail/" + id + ".html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Elements elements = document.getElementsByClass("col-md-2 col-sm-3 col-xs-4");
                    List<Bangumi> bangumis = new ArrayList<>();
                    for (Element element : elements) {
                        Bangumi bangumi = parseBangumi(element.getElementsByTag("p").get(0));
                        bangumis.add(bangumi);
                    }

                    return bangumis;
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Bangumi>> getCategoryBangumis(String category) {
        return null;
    }

    @Override
    public Observable<List<Bangumi>> getNextCategoryBangumis() {
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

    class NicoPlayerUrlBean {
        String url;
        String vid;
        String name;
        String jiexi;
        String time;
        String auth_key;
    }
}
