package com.TyxApp.bangumi.data.source.local;

import android.content.Context;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.util.ExceptionUtil;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SearchWord.class, Bangumi.class, VideoDownloadTask.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public static final String DATABASE_NAME = "BANGUMI_APP";

    public abstract SearchWordDao getSearchWordDao();

    public abstract BangumiDao getBangumiDao();

    public abstract VideoDownloadTaskDao getVideoDownloadStackDao();

    public static synchronized void init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
    }

    public static AppDatabase getInstance() {
        ExceptionUtil.checkNull(INSTANCE, "AppDatabase未初始化");
        return INSTANCE;
    }
}
