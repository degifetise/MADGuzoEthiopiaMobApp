package com.degifetise.madguzoethiopiamobapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {

    private BucketAdapter adapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        db = AppDatabase.getInstance(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_bucket);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new BucketAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_goal);
        fab.setOnClickListener(v -> addMockGoal());

        adapter.setOnItemClickListener(item -> updateMockGoal(item));

        loadGoals();

        return view;
    }

    private void loadGoals() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<BucketItem> items = db.bucketDao().getAllItems();
            getActivity().runOnUiThread(() -> adapter.setItems(items));
        });
    }

    private void addMockGoal() {
        Executors.newSingleThreadExecutor().execute(() -> {
            BucketItem newItem = new BucketItem("Harar Adventure", "2025-05-20", 500.0, "High");
            db.bucketDao().insert(newItem);
            addToCalendar(newItem);
            loadGoals();
        });
    }

    private void addToCalendar(BucketItem item) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        long startMillis;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2025, 4, 20, 7, 30); // May 20, 2025
        startMillis = beginTime.getTimeInMillis();
        long endMillis;
        Calendar endTime = Calendar.getInstance();
        endTime.set(2025, 4, 20, 8, 30);
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Visit " + item.getRegionName());
        values.put(CalendarContract.Events.DESCRIPTION, "Travel plan from Guzo Ethiopia app");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = requireActivity().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri != null) {
            requireActivity().runOnUiThread(() -> 
                Toast.makeText(requireContext(), R.string.calendar_event_added, Toast.LENGTH_SHORT).show());
        }
    }

    private void updateMockGoal(BucketItem item) {
        Executors.newSingleThreadExecutor().execute(() -> {
            item.setPriorityLevel("Medium (Updated)");
            db.bucketDao().update(item);
            loadGoals();
        });
    }
}