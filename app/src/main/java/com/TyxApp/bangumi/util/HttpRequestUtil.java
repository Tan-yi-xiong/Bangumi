package com.TyxApp.bangumi.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpRequestUtil {
    private static OkHttpClient client;

    public static void init() {
        if (client == null) {
            synchronized (HttpRequestUtil.class) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
    }

    public static String getGetRequestResponseBodyString(String url) throws IOException {
        ExceptionUtil.checkNull(client, "HttpRequestUti未初始化");
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request)
                .execute()
                .body()
                .string();
    }

}
