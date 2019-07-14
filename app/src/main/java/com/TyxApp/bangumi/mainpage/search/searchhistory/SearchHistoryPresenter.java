package com.TyxApp.bangumi.mainpage.search.searchhistory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.TyxApp.bangumi.data.source.local.DBHelper;
import com.TyxApp.bangumi.data.source.local.SearchHistoryWordPresistenceContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SearchHistoryPresenter implements SearchHistoryContract.Presenter {
    private SearchHistoryContract.View mView;
    private DBHelper mDBHelper;
    private CompositeDisposable mCompositeDisposable;
    private static final String SQL_BASE_QUERY_WORDS =
            "SELECT " + SearchHistoryWordPresistenceContract.COLUMN_NAME_WORD +
                    " FROM " + SearchHistoryWordPresistenceContract.TABLE_NAME;

    public SearchHistoryPresenter(SearchHistoryContract.View view) {
        mView = view;
        mDBHelper = DBHelper.getInstance();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getWords() {
        final String SQL_QUERY_WORDS_SORT = SQL_BASE_QUERY_WORDS +
                " ORDER BY " + SearchHistoryWordPresistenceContract.COLUMN_NAME_TIME + " DESC";

        mCompositeDisposable.add(Observable.just(SQL_QUERY_WORDS_SORT)
                .map(this::getWordsFormDB)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(words -> {
                    if (words.isEmpty()) {
                        mView.showWordsEmpty();
                    } else {
                        mView.showWords(words);
                    }
                }));

    }

    private List<String> getWordsFormDB(String sql) {
        SQLiteDatabase readableDatabase = mDBHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        List<String> words = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            String word = cursor.getString(0);
            words.add(word);
            cursor.moveToNext();
        }
        return words;
    }

    @Override
    public void getCorrelationWords(String word) {
        final String SQL_SELECT_CORRELATION_WORDS = SQL_BASE_QUERY_WORDS + " WHERE " +
                SearchHistoryWordPresistenceContract.COLUMN_NAME_WORD + " LIKE " + "'%" + word + "%'";

        mCompositeDisposable.add(Observable.just(SQL_SELECT_CORRELATION_WORDS)
                .map(this::getWordsFormDB)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(words -> {
                    if (words.isEmpty()) {
                        mView.showWordsEmpty();
                    } else {
                        mView.showCorrelationWords(words);
                    }
                }));
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
