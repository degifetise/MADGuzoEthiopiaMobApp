package com.degifetise.madguzoethiopiamobapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BucketItem.class, NewsItem.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract BucketDao bucketDao();
    public abstract NewsDao newsDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "guzo_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}