package com.TyxApp.bangumi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ParseUtil {
    public static ObservableTransformer<String, Document> html2Transformer() {
        return observable -> observable.map((Function<String, Document>) s -> {
            String html = HttpRequestUtil.getGetRequestResponseBodyString(s);
            return Jsoup.parse(html);
        });
    }
}
