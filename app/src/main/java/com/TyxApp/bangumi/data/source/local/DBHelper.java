package com.TyxApp.bangumi.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.TyxApp.bangumi.BanghumiApp;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper dbHelper;

    public static final String DB_NAME = "BANGUMI_DATABASE";

    private static final String TEXT_TYPE = " TEXT";

    private static final String UNIQUE_RESTRAIN = " UNIQUE";

    private static final String COMMA_SEP = ",";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String SQL_CREAT_SEARCH_WORD_TABLE =
            "CREATE TABLE " + SearchHistoryWordPresistenceContract.TABLE_NAME + "(" +
                    SearchHistoryWordPresistenceContract.COLUMN_NAME_WORD + TEXT_TYPE + UNIQUE_RESTRAIN + COMMA_SEP +
                    SearchHistoryWordPresistenceContract.COLUMN_NAME_TIME + INTEGER_TYPE +
                    ")";

    private DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private DBHelper(Context appContext) {
        super(appContext, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAT_SEARCH_WORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(BanghumiApp.appContext);
                }
            }
        }
        return dbHelper;
    }

}
