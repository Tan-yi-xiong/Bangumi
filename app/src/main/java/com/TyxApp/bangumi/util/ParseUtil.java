package com.TyxApp.bangumi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ParseUtil {
    public static ObservableTransformer<String, Document> html2Transformer() {
        return html2Transformer("UTF-8");
    }

    public static ObservableTransformer<String, Document> html2Transformer(String charsetName) {
        return observable -> observable.map((Function<String, Document>) s -> {
            String html = HttpRequestUtil.getGetRequestResponseBodyString(s, charsetName);
            return Jsoup.parse(html);
        });
    }

    public static String unicodeToString(String unicode) {
        char[] cw = unicode.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cw.length; i++) {
            char c = cw[i];
            if (c == '\\') {
                StringBuilder ssb = new StringBuilder();
                for (int j = 2; j <= 5; j++) {
                    ssb.append(cw[i+ j]);
                }
                int h = Integer.parseInt(ssb.toString(), 16);
                sb.append((char)h);
                i += 5;
            }else{
                sb.append(cw[i]);
            }
        }
        return sb.toString();
    }
}
