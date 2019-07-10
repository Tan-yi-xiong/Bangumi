package com.TyxApp.bangumi.mainpage.search.SearchResult;


import android.os.Bundle;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.util.ExceptionUtil;

import java.util.List;

public class SearchResultFragmetn extends RecyclerViewFragment implements SearchResultContract.View {
    public static final String ARG_KEY = "P_TYPE";


    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);
    }

    @Override
    public BasePresenter getPresenter() {
        ExceptionUtil.checkNull(getArguments(), "必须通过newInstance方法创建实例");
        String type = getArguments().getString(ARG_KEY);
        BaseBangumiParser parser = null;
        switch (type) {
            case BangumiPresistenceContract
                    .BangumiSource.ZZZFUN:
                parser = new ZzzFun();
            break;
        }
        return new SearchResultPresenter(parser, this);
    }

    @Override
    public void showSearchResultLoadingError() {

    }

    @Override
    public void showSearchResult(List<Bangumi> bangumis) {

    }

    @Override
    public void showMoreSearchResult(List<Bangumi> moreBangumis) {

    }

    public static SearchResultFragmetn newInstance(String presenterType) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, presenterType);
        SearchResultFragmetn fragment = new SearchResultFragmetn();
        fragment.setArguments(args);
        return fragment;
    }
}
