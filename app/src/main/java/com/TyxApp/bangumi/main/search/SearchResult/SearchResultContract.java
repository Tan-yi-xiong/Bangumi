package com.TyxApp.bangumi.main.search.SearchResult;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface SearchResultContract {
    interface Presenter extends BasePresenter {
        void getSearchResult(String word);

        void getMoreSearchResult();
    }

    interface View extends BaseView {

        void showSearchResult(List<Bangumi> bangumis);

        void showMoreSearchResult(List<Bangumi> moreBangumis);

        void noNextSearchResult();
    }
}
