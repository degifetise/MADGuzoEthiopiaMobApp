package com.degifetise.madguzoethiopiamobapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.BucketViewHolder> {

    private List<BucketItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BucketItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<BucketItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BucketViewHolder holder, int position) {
        BucketItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BucketViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;
        private final TextView textDate;
        private final TextView textBudget;
        private final TextView textPriority;

        public BucketViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_region_name);
            textDate = itemView.findViewById(R.id.text_planned_date);
            textBudget = itemView.findViewById(R.id.text_budget);
            textPriority = itemView.findViewById(R.id.text_priority);
        }

        public void bind(final BucketItem item, final OnItemClickListener listener) {
            textName.setText(item.getRegionName());
            textDate.setText(item.getPlannedDate());
            textBudget.setText("$" + item.getEstimatedBudget());
            textPriority.setText(item.getPriorityLevel());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}