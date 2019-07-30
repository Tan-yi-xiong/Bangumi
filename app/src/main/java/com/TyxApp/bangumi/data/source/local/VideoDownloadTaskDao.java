package com.TyxApp.bangumi.data.source.local;

import com.TyxApp.bangumi.data.bean.VideoDownloadTask;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;

@Dao
public interface VideoDownloadTaskDao {
    @Query("SELECT id FROM VIDEODOWNLOADTASK WHERE url = :url")
    int hasSaveInQueue(String url);

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE state = 0 LIMIT 1")
    VideoDownloadTask getAwaitDownloadTasks();

    @Query("SELECT * FROM VIDEODOWNLOADTASK")
    Single<List<VideoDownloadTask>> getDownloadTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> replace(VideoDownloadTask videoDownloadTask);

    @Insert
    long insert(VideoDownloadTask task);

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE id = :id")
    VideoDownloadTask getTaskState(long id);

    @Update
    int update(VideoDownloadTask task);



    @Delete
    int delete(VideoDownloadTask task);
}
