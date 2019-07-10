package com.TyxApp.bangumi.mainpage.search.searchhistory;

import com.TyxApp.bangumi.base.BasePresenter;

import java.util.List;

public interface SearchHistoryContract {
    interface Presenter extends BasePresenter {
        void getWords();

        void getCorrelationWords(String word);
    }

    interface View {
        void showWords(List<String> words);

        void showWordsEmpty();

        void showCorrelationWords(List<String> correlationWords);
    }
}
