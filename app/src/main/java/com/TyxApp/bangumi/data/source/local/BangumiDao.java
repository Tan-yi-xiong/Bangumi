package com.TyxApp.bangumi.data.source.local;

import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

@Dao
public interface BangumiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertBangumi(Bangumi bangumi);


    @Query("UPDATE BANGUMI SET historyTime = :time WHERE vod_id = :videoId AND vod_soure = :sourch")
    int updatetime(int videoId, String sourch, long time);


    @Query("UPDATE BANGUMI SET isFavorite = :isFavorite WHERE vod_id = :videoId AND vod_soure = :sourch")
    Single<Integer> updateFavoriteState(int videoId, String sourch, boolean isFavorite);

    @Query("UPDATE BANGUMI SET isDownLoad = :isDownLoad WHERE vod_id = :videoId AND vod_soure = :sourch")
    Single<Integer> updateDownLoad(int videoId, String sourch, boolean isDownLoad);

    @Query("SELECT dbId FROM BANGUMI WHERE vod_id = :videoId AND vod_soure = :sourch")
    int getBangumiDbId(int videoId, String sourch);



    @Query("SELECT * FROM BANGUMI ORDER BY historyTime DESC")
    Single<List<Bangumi>> getHistoryBangumi();

    @Query("SELECT * FROM BANGUMI WHERE isFavorite = 1")
    Single<List<Bangumi>> getFavoriteBangumi();

    @Query("SELECT * FROM BANGUMI WHERE isDownLoad = 1")
    Single<List<Bangumi>> getDownLoadBangumi();

    @Query("SELECT isFavorite FROM BANGUMI WHERE vod_id = :videoId AND vod_soure = :sourch")
    Flowable<Boolean> hasAddToFavorite(int videoId, String sourch);

    default Single insertOrUpdateTime(Bangumi bangumi) {
        return Single.create((SingleOnSubscribe<Integer>) emitter -> {
            int DdbId = getBangumiDbId(bangumi.getVodId(), bangumi.getVideoSoure());
            emitter.onSuccess(DdbId);
        }).flatMap(dbId -> {
            if (dbId == 0) {
                return insertBangumi(bangumi);
            }
            bangumi.dbId = dbId;
            return Single.just((long) updatetime(bangumi.getVodId(), bangumi.getVideoSoure(), bangumi.getHistoryTime()));
        });
    }

}
