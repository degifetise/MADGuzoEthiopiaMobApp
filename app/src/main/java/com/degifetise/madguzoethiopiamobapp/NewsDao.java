package com.degifetise.madguzoethiopiamobapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NewsItem> newsItems);

    @Query("DELETE FROM news_cache")
    void deleteAll();

    @Query("SELECT * FROM news_cache")
    Single<List<NewsItem>> getAllCachedNews();
}