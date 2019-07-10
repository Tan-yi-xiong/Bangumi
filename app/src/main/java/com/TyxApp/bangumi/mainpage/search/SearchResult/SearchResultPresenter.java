package com.TyxApp.bangumi.mainpage.search.SearchResult;

import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;

import io.reactivex.disposables.CompositeDisposable;

public class SearchResultPresenter implements SearchResultContract.Presenter {
    private BaseBangumiParser bangumiParser;
    private SearchResultContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    public SearchResultPresenter(BaseBangumiParser bangumiParser, SearchResultContract.View view) {
        ExceptionUtil.checkNull(bangumiParser, "bangumiParser不能为空  SearchResultPresenter");
        ExceptionUtil.checkNull(view, "view为空 SearchResultPresenter");
        this.bangumiParser = bangumiParser;
        mView = view;
    }

    @Override
    public void getSearchResult(String word) {
        LogUtil.i(word);
    }

    @Override
    public void getMoreSearchResult() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mCompositeDisposable.dispose();
        mView = null;
    }
}
