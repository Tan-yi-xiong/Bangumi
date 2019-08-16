package com.TyxApp.bangumi.data.source.local;

import com.TyxApp.bangumi.data.bean.VideoDownloadTask;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface VideoDownloadTaskDao {
    @Query("SELECT id FROM VIDEODOWNLOADTASK WHERE url = :url")
    int getId(String url);

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE state = :state LIMIT 1")
    VideoDownloadTask getDownloadTask(int state);

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE state = :state")
    Single<List<VideoDownloadTask>> getRxDownloadTasks(int state);

    @Query("SELECT * FROM VIDEODOWNLOADTASK")
    Single<List<VideoDownloadTask>> getRxDownloadTasks();

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE bangumi_id = :bangumiId AND bangumi_sourch = :sourch")
    Flowable<List<VideoDownloadTask>> getRxDownloadTasks(String bangumiId, String sourch) ;

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE bangumi_id = :bangumiId AND bangumi_sourch = :sourch")
    List<VideoDownloadTask> getDownloadTasks(String bangumiId, String sourch) ;

    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE state != 2")
    Single<List<VideoDownloadTask>> getUnfinishedTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(VideoDownloadTask task);


    @Query("SELECT * FROM VIDEODOWNLOADTASK WHERE id = :id")
    VideoDownloadTask getTaskState(long id);

    @Update
    int update(VideoDownloadTask task);

    @Delete
    int delete(VideoDownloadTask task);

    @Delete
    Single<Integer> deleteTasks(List<VideoDownloadTask> tasks);
}
