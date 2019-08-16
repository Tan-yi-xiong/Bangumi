package com.TyxApp.bangumi.main.search;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.SearchWord;

import java.util.List;

public interface SearchContract {
    interface Presenter extends BasePresenter {
        void getWords();

        void getSimilarityWords(String word);

        void removeWord(SearchWord searchWord);

        void saveWord(String word);
    }

    interface View extends BaseView {
        void showWords(List<SearchWord> words);


        void showSimilarityWords(List<SearchWord> correlationWords);
    }
}
