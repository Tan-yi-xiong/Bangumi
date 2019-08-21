package com.TyxApp.bangumi.util;

import android.content.ContentValues;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class HttpRequestUtil {
    private static OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void init() {
        if (client == null) {
            synchronized (HttpRequestUtil.class) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public static ResponseBody getRespondBody(String url) throws IOException {
        checkNull(client);
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request)
                .execute()
                .body();
    }

    public static String getResponseBodyString(String url) throws IOException {
        return getResponseBodyString(url, "UTF-8");
    }

    public static String getResponseBodyString(String url, String charsetName) throws IOException {
        checkNull(client);
        Request request = new Request.Builder()
                .url(url)
                .build();
        ResponseBody responseBody = client.newCall(request)
                .execute()
                .body();
        return new String(responseBody.bytes(), charsetName);
    }

    public static String postFromResult(String url, RequestBody requestBody) throws IOException {
        checkNull(client);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return client.newCall(request).execute().body().string();
    }

    public static String postJosonResult(String url, ContentValues values) throws IOException {
        checkNull(client);
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (String key : values.keySet()) {
            jsonBuilder.append("\"");
            jsonBuilder.append(key);
            jsonBuilder.append("\":");
            Object value = values.get(key);
            if (value instanceof String) {
                jsonBuilder.append("\"");
                jsonBuilder.append(value.toString());
                jsonBuilder.append("\"");
            } else {
                jsonBuilder.append(value.toString());
            }
            jsonBuilder.append(",");
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);//删除最后一个逗号
        jsonBuilder.append("}");
        RequestBody requestBody = RequestBody.create(JSON, jsonBuilder.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return client.newCall(request)
                .execute()
                .body()
                .string();
    }

    private static void checkNull(OkHttpClient client) {
        ExceptionUtil.checkNull(client, "HttpRequestUti未初始化");
    }

}
