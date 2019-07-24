package com.TyxApp.bangumi.data.bean;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "SEARCH_WORD",
        indices = @Index(value = {"word"}, unique = true))
public class SearchWord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long time;

    private String word;

    public SearchWord(long time, String word) {
        this.time = time;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
