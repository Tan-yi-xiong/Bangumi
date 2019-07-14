package com.TyxApp.bangumi.mainpage.search.SearchResult;

import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getSearchResult(String word) {
        mCompositeDisposable.add(bangumiParser.getSearchResult(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (bangumis.isEmpty()) {
                                mView.showSearchResultEmpty();
                            }else {
                                mView.showSearchResult(bangumis);
                            }
                        },
                        throwable -> mView.showSearchResultLoadingError(throwable)));
    }

    @Override
    public void getMoreSearchResult() {
        mCompositeDisposable.add(bangumiParser.nextSearchResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showMoreSearchResult(bangumis),
                        throwable -> mView.showSearchResultLoadingError(throwable)));
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
