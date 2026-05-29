package com.degifetise.madguzoethiopiamobapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RegionActionBottomSheet extends BottomSheetDialogFragment {

    private final Region region;
    private final ActionCallback callback;

    public interface ActionCallback {
        void onShare(Region region);
        void onFavorite(Region region);
    }

    public RegionActionBottomSheet(Region region, ActionCallback callback) {
        this.region = region;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_region_actions, container, false);

        view.findViewById(R.id.action_share).setOnClickListener(v -> {
            callback.onShare(region);
            dismiss();
        });

        view.findViewById(R.id.action_favorite).setOnClickListener(v -> {
            callback.onFavorite(region);
            dismiss();
        });

        return view;
    }
}