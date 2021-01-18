package com.pwr_lab.passwordshaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_SWITCH_DIGITS = "pref_digits";
    public static final String KEY_SWITCH_ALPHA_LOWER = "pref_alpha_lower";
    public static final String KEY_SWITCH_ALPHA_UPPER = "pref_alpha_upper";
    public static final String KEY_SWITCH_SPECIAL = "pref_special";
    public static final String KEY_SEEK_BAR_LENGTH = "pref_length";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}