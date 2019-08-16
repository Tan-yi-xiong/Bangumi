package com.TyxApp.bangumi.data.source.remote;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Results;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface IBangumiParser {
    /**
     * 获取网站主页番剧信息
     * map的泛型string为组名, 默认的主页adapter会遍历然后设置
     * 注意：主页默认adapter有轮播图, 组名一定要为banner
     */
    Observable<Map<String, List<Bangumi>>> getHomePageBangumiData();


    /**
     * 获取网站搜索结果
     *
     */
    Observable<List<Bangumi>> getSearchResult(String word);

    /**
     * 获取搜索结果下一页数据(分批加载数据), 多数网站都是分批加载数据。
     * 一般逻辑为先调用getSearchResult()方法, 在getSearchResult()方法里同时记录页面所有的下一页url(也可以遍历所有url把所有数据一次加载完, 但是不推荐)按顺序保存 ,
     * 然后按顺序取出加载返回包装好的数据。
     *
     */
    Observable<Results> nextSearchResult();

    /**
     * 根据id获取番剧的简介, 播放页面使用。
     *
     * @param id
     */
    Observable<BangumiInfo> getInfo(String id);

    /**
     * 根据id获取该番剧所有集的标题名字, 一般网站为第一集, 第二集....。
     * 有的网站在获取集数是时候能同时获取到对应集数视频的url, 这时应该把url按顺序存起来, 这样点击对应集数就不用再去请求解析视频url。
     *
     * @param id
     */
    Observable<List<TextItemSelectBean>> getJiList(String id);

    /**
     * 根据传进来的集去获取该id番剧该集的播放地址
     *
     */
    Observable<VideoUrl> getplayerUrl(String id, int ji);

    Observable<List<Bangumi>> getRecommendBangumis(String id);

    /**
     *获取分类结果和下一页结果的方法, 逻辑与搜索结果一样
     *
     */
    Observable<List<Bangumi>> getCategoryBangumis(String category);

    Observable<Results> getNextCategoryBangumis();

    Observable<List<CategorItem>> getCategorItems();

    Observable<List<List<Bangumi>>> getBangumiTimeTable();

}
