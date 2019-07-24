package com.TyxApp.bangumi.main.search;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.SearchWord;


public interface SearchContract {
    interface Presenter extends BasePresenter {
        void saveWord(SearchWord word);
    }

}
