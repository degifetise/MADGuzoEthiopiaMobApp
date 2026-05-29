package com.degifetise.madguzoethiopiamobapp;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    private int regionId;
    private CompositeDisposable disposables = new CompositeDisposable();
    private NewsAdapter newsAdapter;
    private ShimmerFrameLayout shimmerContainer;
    private Button btnRetry;
    private NewsApiService apiService;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        db = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        ImageView detailsImage = findViewById(R.id.details_image);
        TextView detailsText = findViewById(R.id.details_text);
        Button btnGenerate = findViewById(R.id.btn_generate_itinerary);
        shimmerContainer = findViewById(R.id.shimmer_view_container);
        btnRetry = findViewById(R.id.btn_retry_news);

        RecyclerView recyclerViewNews = findViewById(R.id.recycler_view_news);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newsAdapter = new NewsAdapter();
        recyclerViewNews.setAdapter(newsAdapter);

        if (getIntent() != null && getIntent().getExtras() != null) {
            regionId = getIntent().getExtras().getInt(getString(R.string.region_id_extra), -1);
            collapsingToolbarLayout.setTitle("Region " + regionId);
            detailsText.setText(getString(R.string.details_title, regionId));

            // Chapter 7: Load image with Glide from online source
            String imageUrl = "https://raw.githubusercontent.com/degifetise/MADGuzoEthiopiaMobApp/main/images/region_" + regionId + ".jpg";
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(detailsImage);
        }

        btnGenerate.setOnClickListener(v -> generateItinerary());
        btnRetry.setOnClickListener(v -> fetchNewsFromApi());

        setupRetrofit();
        loadNews();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(NewsApiService.class);
    }

    private void loadNews() {
        // First, show cached news if available
        disposables.add(db.newsDao().getAllCachedNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cachedNews -> {
                    if (!cachedNews.isEmpty()) {
                        newsAdapter.setNewsList(cachedNews);
                        shimmerContainer.stopShimmer();
                        shimmerContainer.setVisibility(View.GONE);
                    }
                    fetchNewsFromApi();
                }, throwable -> fetchNewsFromApi()));
    }

    private void fetchNewsFromApi() {
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        btnRetry.setVisibility(View.GONE);

        disposables.add(apiService.getCulturalNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsList -> {
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    newsAdapter.setNewsList(newsList);
                    cacheNews(newsList);
                }, throwable -> {
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    if (newsAdapter.getItemCount() == 0) {
                        btnRetry.setVisibility(View.VISIBLE);
                        Toast.makeText(this, R.string.error_loading_news, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void cacheNews(List<NewsItem> newsList) {
        Schedulers.io().scheduleDirect(() -> {
            db.newsDao().deleteAll();
            db.newsDao().insertAll(newsList);
        });
    }

    private void generateItinerary() {
        String fileName = "Itinerary_Region_" + regionId + ".txt";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write("Guzo Ethiopia Mobile App - Itinerary\n");
            osw.write("------------------------------------\n");
            osw.write("Region ID: " + regionId + "\n");
            osw.write("Planned Date: To be decided\n");
            osw.write("Explore Ethiopia with us!");
            Toast.makeText(this, getString(R.string.itinerary_generated), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.itinerary_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}