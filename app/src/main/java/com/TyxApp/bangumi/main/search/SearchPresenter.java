package com.TyxApp.bangumi.main.search;

import android.text.TextUtils;

import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.SearchWordDao;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private SearchWordDao mSearchWordDao;
    private Disposable searchDisposable;

    private boolean isNext;

    public SearchPresenter(SearchContract.View view) {
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
        if (!TextUtils.isEmpty(word)) {
            if (searchDisposable != null && !searchDisposable.isDisposed()) {//防止快速输入
                return;
            }
            searchDisposable = Observable.just(word)
                    .map(URLEncoder::encode)
                    .map(encodeWord -> "http://www.zzzfun.com/index.php/ajax/suggest?mid=1&wd=" + encodeWord + "&limit=6")
                    .map(HttpRequestUtil::getResponseBodyString)
                    .flatMap(jsonData -> {
                        JsonArray jsonArray = new JsonParser().parse(jsonData)
                                .getAsJsonObject()
                                .get("list")
                                .getAsJsonArray();
                        return Observable.fromIterable(jsonArray);
                    })
                    .map(jsonElement -> {
                        String result = jsonElement.getAsJsonObject().get("name").getAsString();
                        SearchWord searchWord = new SearchWord(System.currentTimeMillis(), result);
                        searchWord.setFromNet(true);
                        return searchWord;
                    })
                    .take(6)
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            searchWords -> {
                                if (!isNext) {
                                    mView.showSimilarityWords(searchWords);
                                } else {
                                    isNext = false;
                                }
                            },
                            throwable -> LogUtil.i(throwable.toString()));
        } else {
            if (!searchDisposable.isDisposed()) {//防止网络数据加载完回调冲掉这组数据
                isNext = true;
            }
            word = "%" + word + "%";
            mCompositeDisposable.add(mSearchWordDao.getSimilarityWords(word)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            searchWords -> mView.showSimilarityWords(searchWords),
                            throwable -> mView.showResultError(throwable)));
        }

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
