package com.TyxApp.bangumi.main.search;

import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.SearchWordDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchHistoryPresenter implements SearchHistoryContract.Presenter {
    private com.TyxApp.bangumi.main.search.SearchHistoryContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private SearchWordDao mSearchWordDao;

    public SearchHistoryPresenter(SearchHistoryContract.View view) {
        mView = view;
        mSearchWordDao = AppDatabase.getInstance().getSearchWordDao();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getWords() {
        mCompositeDisposable.add(mSearchWordDao.getSearchWords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchWords -> {
                            if (searchWords.isEmpty()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showWords(searchWords);
                            }
                        },
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void getSimilarityWords(String word) {
        word = "%" + word + "%";
        mCompositeDisposable.add(mSearchWordDao.getSimilarityWords(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchWords -> mView.showSimilarityWords(searchWords),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void removeWord(SearchWord word) {
        mCompositeDisposable.add(mSearchWordDao.delete(word).subscribeOn(Schedulers.io()).subscribe());
    }

    @Override
    public void saveWord(String word) {
        mCompositeDisposable.add(mSearchWordDao.insert(new SearchWord(System.currentTimeMillis(), word))
                .subscribeOn(Schedulers.io())
                .subscribe());
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
