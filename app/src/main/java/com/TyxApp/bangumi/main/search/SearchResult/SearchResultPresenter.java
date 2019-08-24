package com.TyxApp.bangumi.main.search.SearchResult;

import com.TyxApp.bangumi.parse.ISearchParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class SearchResultPresenter implements SearchResultContract.Presenter {
    private ISearchParser mSearchParser;
    private SearchResultContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    public SearchResultPresenter(ISearchParser searchParser, SearchResultContract.View view) {
        ExceptionUtil.checkNull(searchParser, "bangumiParser不能为空  SearchResultPresenter");
        ExceptionUtil.checkNull(view, "view为空 SearchResultPresenter");
        this.mSearchParser = searchParser;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getSearchResult(String word) {
        mCompositeDisposable.add(mSearchParser.getSearchResult(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> {
                            if (bangumis.isEmpty()) {
                                mView.showResultEmpty();
                            }else {
                                mView.showSearchResult(bangumis);
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void getMoreSearchResult() {
        mCompositeDisposable.add(mSearchParser.nextSearchResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isNull()) {
                                mView.noNextSearchResult();
                            } else {
                                mView.showMoreSearchResult(results.getResult());
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mCompositeDisposable.dispose();
        mView = null;
        mSearchParser = null;
    }
}
