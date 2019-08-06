package com.TyxApp.bangumi.main.search.searchhistory;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.SearchWord;

import java.util.List;

public interface SearchHistoryContract {
    interface Presenter extends BasePresenter {
        void getWords();

        void getSimilarityWords(String word);

        void removeWord(SearchWord searchWord);
    }

    interface View extends BaseView {
        void showWords(List<SearchWord> words);


        void showSimilarityWords(List<SearchWord> correlationWords);
    }
}
