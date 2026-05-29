package com.degifetise.madguzoethiopiamobapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> implements Filterable {

    private List<Region> regionList;
    private List<Region> regionListFull;
    private final OnRegionClickListener listener;

    public interface OnRegionClickListener {
        void onRegionClick(Region region, ImageView imageView);
        void onRegionLongClick(Region region);
    }

    public RegionAdapter(List<Region> regionList, OnRegionClickListener listener) {
        this.regionList = regionList;
        this.regionListFull = new ArrayList<>(regionList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_region, parent, false);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        Region region = regionList.get(position);
        holder.bind(region, listener);
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    public void updateList(List<Region> newList) {
        this.regionList = newList;
        this.regionListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return regionFilter;
    }

    private final Filter regionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Region> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(regionListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Region item : regionListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            regionList.clear();
            regionList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class RegionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageBanner;
        private final TextView textTitle;

        public RegionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBanner = itemView.findViewById(R.id.image_banner);
            textTitle = itemView.findViewById(R.id.text_title);
        }

        public void bind(final Region region, final OnRegionClickListener listener) {
            textTitle.setText(region.getName());
            
            // Chapter 7: Load image with Glide from online source
            String imageUrl = "https://raw.githubusercontent.com/degifetise/MADGuzoEthiopiaMobApp/main/images/region_" + region.getId() + ".jpg";
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(imageBanner);

            itemView.setOnClickListener(v -> listener.onRegionClick(region, imageBanner));
            itemView.setOnLongClickListener(v -> {
                listener.onRegionLongClick(region);
                return true;
            });
        }
    }
}