package com.degifetise.madguzoethiopiamobapp;

import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BucketDao {
    @Insert
    void insert(BucketItem item);

    @Update
    void update(BucketItem item);

    @Delete
    void delete(BucketItem item);

    @Query("SELECT * FROM bucket_list ORDER BY id DESC")
    List<BucketItem> getAllItems();

    @Query("SELECT * FROM bucket_list")
    Cursor selectAll();

    @Query("SELECT * FROM bucket_list WHERE id = :id")
    Cursor selectById(long id);
}