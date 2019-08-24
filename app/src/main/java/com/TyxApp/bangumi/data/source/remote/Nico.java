package com.TyxApp.bangumi.data.source.remote;

import android.os.Build;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Result;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.parse.ISearchParser;
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
import java.util.Map;

import androidx.annotation.RequiresApi;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class Nico implements ISearchParser {
    private String baseUrl = "http://www.nicotv.me";
    private List<String> jiUrls;
    private List<String> searchMoreHtml;

    private Nico() {
        searchMoreHtml = new ArrayList<>();
    }

    public static Nico getInstance() {
       return new Nico();
    }


    @Override
    public Observable<List<Bangumi>> getSearchResult(String word) {
        searchMoreHtml.clear();
        String url = baseUrl + "/video/search/" + word + ".html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    saveSearchResultUrls(document);
                    return parseSearchPageBangumis(document);
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
        bangumi.setVideoId(id);
        return bangumi;
    }

    @Override
    public Observable<Result<List<Bangumi>>> nextSearchResult() {
        if (searchMoreHtml.isEmpty()) {
            return Observable.just(new Result<>(true, null));
        }
        String nextUrl = searchMoreHtml.remove(0);
        return Observable.just(nextUrl)
                .compose(ParseUtil.html2Transformer())
                .map(document -> new Result<>(false, parseSearchPageBangumis(document)))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<BangumiInfo> getInfo(String id) {
        return Observable.just(baseUrl + "/video/detail/" + id +".html")
                .compose(ParseUtil.html2Transformer())
                .map(document -> {
                    Elements elements = document.getElementsByClass("ff-text-right");
                    String cast = parseChild(elements.get(0)).replaceAll(" ", "\n");
                    String staff = elements.get(1).child(0).text();
                    String type = parseChild(elements.get(2));
                    String niandai = elements.get(4).child(0).text();
                    String intro = document.getElementsByClass("vod-content ff-collapse text-justify").text();
                    intro = intro.replaceAll("&.*?;", "");
                    BangumiInfo bangumiInfo = new BangumiInfo(niandai, cast, staff, type, intro, "");
                    return bangumiInfo;
                })
                .subscribeOn(Schedulers.io());
    }

    private String parseChild(Element element) {
        StringBuilder builder = new StringBuilder();
        for (Element child : element.children()) {
            builder.append(child.text());
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public Observable<List<TextItemSelectBean>> getJiList(String id) {
        String url = baseUrl + "/video/play/" + id + "-1-1.html";
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .concatMapIterable(document -> document.getElementsByClass("tab-content ff-playurl-dropdown").get(0).children())
                .filter(element -> !element.toString().contains("提取码"))
                .take(1)
                .map(jiListElement -> {
                    List<TextItemSelectBean> selectBeans = new ArrayList<>();
                    jiUrls = new ArrayList<>();
                    for (Element child : jiListElement.children()) {
                        String text = child.getElementsByTag("a").text();
                        String jiUrl = baseUrl + child.getElementsByTag("a").attr("href");
                        TextItemSelectBean bean = new TextItemSelectBean(text);
                        selectBeans.add(bean);
                        jiUrls.add(jiUrl);
                    }
                    return selectBeans;
                })
                .subscribeOn(Schedulers.io());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Observable<VideoUrl> getplayerUrl(String id, int ji) {
        String url = jiUrls.get(ji);
        return Observable.just(url)
                .compose(ParseUtil.html2Transformer())
                .flatMap(document -> {
                    Element element = document.getElementById("cms_player").child(0);
                    String videoHtmlUrl = baseUrl + element.attr("src");
                    String html = HttpRequestUtil.getResponseBodyString(videoHtmlUrl);
                    return Observable.just(html);
                })
                .map(html -> {
                    html = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
                    NicoPlayerUrlBean bean = new Gson().fromJson(html, NicoPlayerUrlBean.class);
                    return parsePlayerUrl(id, ji, bean);
                })
                .subscribeOn(Schedulers.io());

    }

    private VideoUrl parsePlayerUrl(String id, int ji, NicoPlayerUrlBean bean) throws IOException {
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
            url = jiUrls.get(ji);
            isHtmlUrl = true;
        }
        VideoUrl videoUrl = new VideoUrl(url);
        videoUrl.setHtml(isHtmlUrl);
        return videoUrl;
    }

    private String parseVideoUrlFormhtml(String url) throws IOException {
        String data = HttpRequestUtil.getResponseBodyString(url);
        Document d = Jsoup.parse(data);
        Element e = d.getElementsByTag("script").get(1);
        data = e.toString();
        data = data.substring(data.lastIndexOf("{"), data.lastIndexOf(","));
        url = data.substring(data.indexOf("\"") + 1, data.lastIndexOf("\""));
        return url;
    }

    @Override
    public Observable<List<Bangumi>> getRecommendBangumis(String id) {
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
    public Observable<Result<BaseDanmakuParser>> getDanmakuParser(String id, int ji) {
        return Observable.just(new Result<>(true, null));
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
