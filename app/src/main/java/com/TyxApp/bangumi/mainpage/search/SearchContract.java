package com.TyxApp.bangumi.mainpage.search;

import com.TyxApp.bangumi.base.BasePresenter;



public interface SearchContract {
    interface Presenter extends BasePresenter {
        void saveWord(String word);
    }

}
