package com.TyxApp.bangumi.data.source.local;

import com.TyxApp.bangumi.data.bean.SearchWord;

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
public interface SearchWordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(SearchWord searchWord);

    @Delete
    Single<Integer> delete(SearchWord searchWord);


    @Query("SELECT * FROM SEARCH_WORD ORDER BY time DESC")
    Single<List<SearchWord>> getSearchWords();

    @Query("SELECT * FROM SEARCH_WORD WHERE word LIKE :word")
    Single<List<SearchWord>> getSimilarityWords(String word);
}
