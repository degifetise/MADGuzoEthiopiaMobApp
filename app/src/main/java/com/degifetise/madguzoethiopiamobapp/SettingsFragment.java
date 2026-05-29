package com.degifetise.madguzoethiopiamobapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private PreferenceHelper preferenceHelper;
    private ImageView imgAvatar;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Void> pickContact;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imgAvatar.setImageURI(uri);
            }
        });

        pickContact = registerForActivityResult(new ActivityResultContracts.PickContact(), uri -> {
            if (uri != null) {
                queryContactEmail(uri);
            }
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        pickContact.launch(null);
                    } else {
                        Toast.makeText(requireContext(), R.string.contact_permission_denied, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        preferenceHelper = new PreferenceHelper(requireContext());

        imgAvatar = view.findViewById(R.id.img_avatar);
        imgAvatar.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        SwitchCompat switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchDarkMode.setChecked(preferenceHelper.isDarkMode());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceHelper.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        Button btnSelectBuddy = view.findViewById(R.id.btn_select_buddy);
        btnSelectBuddy.setOnClickListener(v -> checkContactPermission());

        Spinner spinnerRegion = view.findViewById(R.id.spinner_default_region);
        setupSpinner(spinnerRegion);

        return view;
    }

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            pickContact.launch(null);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void queryContactEmail(Uri contactUri) {
        ContentResolver cr = requireActivity().getContentResolver();
        try (Cursor cursor = cr.query(contactUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                if (idIndex != -1) {
                    String id = cursor.getString(idIndex);
                    try (Cursor emailCursor = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null)) {
                        
                        if (emailCursor != null && emailCursor.moveToFirst()) {
                            int emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                            if (emailIndex != -1) {
                                String email = emailCursor.getString(emailIndex);
                                fetchBudgetAndSendEmail(email);
                            }
                        } else {
                            Toast.makeText(requireContext(), R.string.no_email_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    private void fetchBudgetAndSendEmail(String email) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<BucketItem> items = AppDatabase.getInstance(requireContext()).bucketDao().getAllItems();
            String regionName = "Ethiopia";
            double budget = 0.0;
            if (!items.isEmpty()) {
                regionName = items.get(0).getRegionName();
                budget = items.get(0).getEstimatedBudget();
            }

            String finalRegionName = regionName;
            double finalBudget = budget;
            requireActivity().runOnUiThread(() -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_body, finalRegionName, finalBudget));
                
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            });
        });
    }

    private void setupSpinner(Spinner spinner) {
        List<String> regionNames = new ArrayList<>();
        regionNames.add(getString(R.string.none));
        regionNames.add("Harar");
        regionNames.add("Lalibela");
        regionNames.add("Gondar");
        regionNames.add("Axum");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, regionNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int currentDefault = preferenceHelper.getDefaultRegion();
        if (currentDefault != -1) {
            spinner.setSelection(currentDefault);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    preferenceHelper.setDefaultRegion(-1);
                } else {
                    preferenceHelper.setDefaultRegion(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}