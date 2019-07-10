package com.TyxApp.bangumi.mainpage.search.SearchResult;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;

import java.util.List;

public interface SearchResultContract {
    interface Presenter extends BasePresenter {
        void getSearchResult(String word);

        void getMoreSearchResult();
    }

    interface View {
        void showSearchResultLoadingError();

        void showSearchResult(List<Bangumi> bangumis);

        void showMoreSearchResult(List<Bangumi> moreBangumis);
    }
}
