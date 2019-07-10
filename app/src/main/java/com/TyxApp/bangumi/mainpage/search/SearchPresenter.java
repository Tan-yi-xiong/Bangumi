package com.TyxApp.bangumi.mainpage.search;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.TyxApp.bangumi.data.source.local.DBHelper;
import com.TyxApp.bangumi.data.source.local.SearchHistoryWordPresistenceContract;

public class SearchPresenter implements SearchContract.Presenter {
    private DBHelper mDBHelper;

    public SearchPresenter() {
        mDBHelper = DBHelper.getInstance();
    }

    @Override
    public void saveWord(String word) {
        //插入一条数据很快, 所以直接主线程完成
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SearchHistoryWordPresistenceContract.COLUMN_NAME_WORD, word);
        values.put(SearchHistoryWordPresistenceContract.COLUMN_NAME_TIME, System.currentTimeMillis());
        writableDatabase.replace(SearchHistoryWordPresistenceContract.TABLE_NAME, null, values);
    }
    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }
}
