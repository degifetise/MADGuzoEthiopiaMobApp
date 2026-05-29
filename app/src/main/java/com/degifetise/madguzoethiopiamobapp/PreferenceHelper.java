package com.degifetise.madguzoethiopiamobapp;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class PreferenceHelper {
    private static final String PREF_NAME = "guzo_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_DEFAULT_REGION = "default_region";

    private SharedPreferences sharedPreferences;

    public PreferenceHelper(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void setDarkMode(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDefaultRegion(int regionId) {
        sharedPreferences.edit().putInt(KEY_DEFAULT_REGION, regionId).apply();
    }

    public int getDefaultRegion() {
        return sharedPreferences.getInt(KEY_DEFAULT_REGION, -1);
    }
}