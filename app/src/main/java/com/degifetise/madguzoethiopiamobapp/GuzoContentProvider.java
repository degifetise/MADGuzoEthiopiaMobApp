package com.degifetise.madguzoethiopiamobapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GuzoContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.degifetise.madguzoethiopiamobapp.provider";
    public static final String TABLE_NAME = "regions";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    private static final int REGIONS = 1;
    private static final int REGION_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, REGIONS);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", REGION_ID);
    }

    private BucketDao bucketDao;

    @Override
    public boolean onCreate() {
        bucketDao = AppDatabase.getInstance(getContext()).bucketDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case REGIONS:
                cursor = bucketDao.selectAll();
                if (getContext() != null) {
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null; // Not implemented for this challenge
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0; // Not implemented
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0; // Not implemented
    }
}