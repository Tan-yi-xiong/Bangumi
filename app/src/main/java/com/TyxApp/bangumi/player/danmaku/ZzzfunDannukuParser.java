package com.TyxApp.bangumi.player.danmaku;

import android.graphics.Color;

import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource;
import master.flame.danmaku.danmaku.util.IOUtils;
import okhttp3.internal.Util;

public class ZzzfunDannukuParser extends BaseDanmakuParser {

    @Override
    protected IDanmakus parse() {
        if (mDataSource != null) {
            AndroidFileSource source = (AndroidFileSource) mDataSource;
            Danmakus danmakus = new Danmakus();
            String jsonData = IOUtils.getString(source.data());
            JsonElement jsonElement = new JsonParser().parse(jsonData)
                    .getAsJsonObject()
                    .get("data");
            if (!jsonElement.isJsonNull()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement danmakuJsons : jsonArray) {
                    JsonArray danmakuJson = danmakuJsons.getAsJsonArray();
                    //["12.09","1","#ffffff","443397417","\u3002\u3002\u3002"]
                    int type = parseInteger(danmakuJson.get(1).getAsString());
                    long time = (long) (parseFloat(danmakuJson.get(0).getAsString()) * 1000);
                    int color = Color.parseColor(danmakuJson.get(2).getAsString());
                    String nierong = danmakuJson.get(4).getAsString();
                    BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(type, mContext);
                    if (danmaku != null) {
                        danmaku.setTime(time);
                        danmaku.textColor = color;
                        danmaku.text = nierong;
                        danmaku.textSize = 25.0f * (mDispDensity - 0.6f);
                        danmaku.flags = mContext.mGlobalFlagValues;
                        danmaku.setTimer(mTimer);
                        danmakus.addItem(danmaku);
                    }
                }
                return danmakus;
            }
        }
        return null;
    }

    private float parseFloat(String floatStr) {
        try {
            return Float.parseFloat(floatStr);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    private int parseInteger(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
