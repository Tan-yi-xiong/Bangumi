package com.TyxApp.bangumi.parse;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.bean.Result;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 主页内容的解析,要实现主页必须要网站有时间表, 分类这些信息{@link com.TyxApp.bangumi.main.bangumi.BangumiFragment}, {@link com.TyxApp.bangumi.categoryresult.CategoryResultActivity}调用
 *
 *
 */
public interface IHomePageParse {
    /**
     * 获取网站主页番剧信息
     * map的泛型string为组名, 默认的主页adapter会遍历然后设置
     * 注意：主页默认adapter有轮播图, 组名一定要为banner
     */
    Observable<Map<String, List<Bangumi>>> getHomePageBangumiData();

    /**
     * 获取分类的类型{@link com.TyxApp.bangumi.main.category.CategoryFragment} 调用
     *
     * @return
     */
    Observable<List<CategorItem>> getCategorItems();

    /**
     * 根据传进来的类别词解析成番剧集合 {@link com.TyxApp.bangumi.categoryresult.CategoryResultActivity}调用
     *
     * @param category 类别词
     */
    Observable<List<Bangumi>> getCategoryBangumis(String category);

    /**
     *类型结果页滑到底部会调用, 没有更多了将{@link Result} 属性isNull设为true就不会再调用的
     *
     */
    Observable<Result<List<Bangumi>>> getNextCategoryBangumis();

    /**
     * 时间表, 按顺序将周一到周日的番剧集合排好{@link com.TyxApp.bangumi.main.timetable.TimeTableFragment}调用
     */
    Observable<List<List<Bangumi>>> getBangumiTimeTable();
}
