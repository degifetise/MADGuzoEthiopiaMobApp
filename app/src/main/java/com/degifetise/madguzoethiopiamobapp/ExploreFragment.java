package com.degifetise.madguzoethiopiamobapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreFragment extends Fragment {

    private RegionAdapter adapter;
    private List<Region> regions;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    // Handle permission result if needed
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_explore);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        regions = new ArrayList<>();
        regions.add(new Region(1, "Harar", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(2, "Lalibela", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(3, "Gondar", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(4, "Axum", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(5, "Bale Mountains", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(6, "Danakil Depression", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(7, "Simien Mountains", android.R.drawable.ic_menu_gallery));
        regions.add(new Region(8, "Omo Valley", android.R.drawable.ic_menu_gallery));

        adapter = new RegionAdapter(regions, new RegionAdapter.OnRegionClickListener() {
            @Override
            public void onRegionClick(Region region, android.widget.ImageView imageView) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getString(R.string.region_id_extra), region.getId());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), imageView, "transition_image");
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onRegionLongClick(Region region) {
                showRegionActions(region);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void showRegionActions(Region region) {
        RegionActionBottomSheet bottomSheet = new RegionActionBottomSheet(region, new RegionActionBottomSheet.ActionCallback() {
            @Override
            public void onShare(Region region) {
                checkNotificationPermissionAndShow(region);
            }

            @Override
            public void onFavorite(Region region) {
                showUndoSnackbar(region);
            }
        });
        bottomSheet.show(getChildFragmentManager(), "RegionActions");
    }

    private void checkNotificationPermissionAndShow(Region region) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showShareNotification(requireContext(), region.getName());
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            NotificationHelper.showShareNotification(requireContext(), region.getName());
        }
    }

    private void showUndoSnackbar(Region region) {
        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.removed_favorites), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> {
            // Logic to undo removal (not actually removing in this mock)
        });
        snackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.explore_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_za) {
            Collections.sort(regions, (r1, r2) -> r2.getName().compareToIgnoreCase(r1.getName()));
            adapter.updateList(new ArrayList<>(regions));
            return true;
        } else if (id == R.id.action_culinary_fact) {
            new CulinaryFactDialogFragment().show(getChildFragmentManager(), "CulinaryFact");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}