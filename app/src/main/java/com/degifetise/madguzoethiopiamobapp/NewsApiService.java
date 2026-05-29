package com.degifetise.madguzoethiopiamobapp;

import java.util.List;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface NewsApiService {
    // Mock Gist URL or static JSON hosting
    @GET("path/to/cultural_news.json")
    Single<List<NewsItem>> getCulturalNews();
}