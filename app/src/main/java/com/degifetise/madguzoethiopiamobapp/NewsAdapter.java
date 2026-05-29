package com.degifetise.madguzoethiopiamobapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList = new ArrayList<>();

    public void setNewsList(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return Math.min(newsList.size(), 3); // Requirement: Top 3 news events
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descText;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.news_title);
            descText = itemView.findViewById(R.id.news_description);
        }

        public void bind(NewsItem item) {
            titleText.setText(item.getTitle());
            descText.setText(item.getDescription());
        }
    }
}