package com.TyxApp.bangumi.parse;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.Result;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;

import java.util.List;

import io.reactivex.Observable;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * 解析番剧视频的接口, {@link com.TyxApp.bangumi.player.PlayerActivity}调用
 * 调用的解析顺序为 先获取番剧信息getInfo,
 * 无论成功与否都会调用集数获取getJiList, 如果获取集数失败不再调用,
 * 否则继续调用getRecommendBangumis和getplayerUrl, 获取推荐番剧和视频播放地址
 * 视频地址获取成功会调用获取弹幕, 没有的将返回类{@link Result<BaseDanmakuParser>}的必须将isNull属性设为true
 */
public interface IVideoParse {
    /**
     * 获取番剧的信息
     */
    Observable<BangumiInfo> getInfo(String id);

    /**
     * 获取集数
     */
    Observable<List<TextItemSelectBean>> getJiList(String id);

    /**
     * 获取推荐番剧
     */
    Observable<List<Bangumi>> getRecommendBangumis(String id);

    /**
     * 获取视频播放地址
     *
     * @param id
     * @param ji 第几集
     */
    Observable<VideoUrl> getplayerUrl(String id, int ji);

    /**
     * 弹幕获取
     */
    Observable<Result<BaseDanmakuParser>> getDanmakuParser(String id, int ji);
}
