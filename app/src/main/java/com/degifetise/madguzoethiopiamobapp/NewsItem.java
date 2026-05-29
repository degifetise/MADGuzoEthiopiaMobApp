package com.degifetise.madguzoethiopiamobapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "news_cache")
public class NewsItem {
    @PrimaryKey(autoGenerate = true)
    private int dbId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("url")
    private String url;

    public NewsItem(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public int getDbId() { return dbId; }
    public void setDbId(int dbId) { this.dbId = dbId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}