package com.TyxApp.bangumi.parse;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.Result;

import java.util.List;

import io.reactivex.Observable;

/**
 * 搜索结果解析{@link com.TyxApp.bangumi.main.search.SearchActivity}调用
 */
public interface ISearchParser extends IVideoParse {
    /**
     * 获取搜索结果
     *
     */
    Observable<List<Bangumi>> getSearchResult(String word);

    /**
     * 搜索结果页滑到底部会调用, 没有更多了将{@link Result} 属性isNull设为true就不会再调用的
     *
     */
    Observable<Result<List<Bangumi>>> nextSearchResult();
}
