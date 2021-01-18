package com.pwr_lab.passwordshaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final float accelerationThreshold = 2;
    // in ms
    private static final long accelerationGapInterval = 500;

    private SensorManager sensorManager;
    private Sensor sensorAcc;
    private long timeLastShake;
    private boolean hasShaken = false;

    private PasswordGenerator passGenerator;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // change from splash screen to normal theme
        setTheme(R.style.Theme_PasswordShaker);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_container, new GuideFragment())
                    .commit();
        }

        // sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // password generator
        passGenerator = new PasswordGenerator();
        // set defaults
        Resources res = getResources();
        passGenerator.setDefaultDigits(res.getBoolean(R.bool.pref_digits_default))
                .setDefaultLower(res.getBoolean(R.bool.pref_alpha_lower_default))
                .setDefaultUpper(res.getBoolean(R.bool.pref_alpha_upper_default))
                .setDefaultSpecial(res.getBoolean(R.bool.pref_special_default))
                .setDefaultLen(res.getInteger(R.integer.pref_length_default));

        // save default settings values
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    private void setSettings() {
        // shared prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefDigits = prefs.getBoolean(SettingsActivity.KEY_SWITCH_DIGITS, true);
        boolean prefAlphaLower = prefs.getBoolean(SettingsActivity.KEY_SWITCH_ALPHA_LOWER, true);
        boolean prefAlphaUpper = prefs.getBoolean(SettingsActivity.KEY_SWITCH_ALPHA_UPPER, false);
        boolean prefSpecial = prefs.getBoolean(SettingsActivity.KEY_SWITCH_SPECIAL, false);
        int prefLength = prefs.getInt(SettingsActivity.KEY_SEEK_BAR_LENGTH, passGenerator.getLen());

        // if there is no charset checked, set default charsets
        if (noneTrue(new boolean[]{prefDigits, prefAlphaLower, prefAlphaUpper, prefSpecial})) {
            passGenerator.setDefaultCharsets();
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putBoolean(SettingsActivity.KEY_SWITCH_DIGITS, passGenerator.getDigits())
                    .putBoolean(SettingsActivity.KEY_SWITCH_ALPHA_LOWER, passGenerator.getLower())
                    .putBoolean(SettingsActivity.KEY_SWITCH_ALPHA_UPPER, passGenerator.getUpper())
                    .putBoolean(SettingsActivity.KEY_SWITCH_SPECIAL, passGenerator.getSpecial());
            prefsEdit.apply();
            Toast.makeText(this, "Invalid options changed to defaults", Toast.LENGTH_SHORT).show();
        }
        else {
            passGenerator.setDigits(prefDigits)
                    .setLower(prefAlphaLower)
                    .setUpper(prefAlphaUpper)
                    .setSpecial(prefSpecial)
                    .setLen(prefLength);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ps_toolbar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // Navigate to given fragment
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.main_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void onShake() {
        String newPassword = passGenerator.next();
        if (!hasShaken) {
            mainFragment = MainFragment.newInstance(newPassword);
            navigateTo(mainFragment, false);
            hasShaken = true;
        }
        else {
            mainFragment.setPassword(newPassword);
        }

        timeLastShake = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;
        if (isShakeFrozen()) return;
        if (!isAccelerationChanged(event)) return;

        onShake();
    }

    private boolean isAccelerationChanged(SensorEvent event) {
        float x = event.values[0],
                y = event.values[1],
                z = event.values[2];

        // Well, let's skip Math.sqrt by comparing squares
        float squareVectorLen = x * x + y * y + z * z;
        return squareVectorLen > accelerationThreshold * accelerationThreshold;
    }

    private boolean isShakeFrozen() {
        return System.currentTimeMillis() - timeLastShake < accelerationGapInterval;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_GAME);
        setSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private boolean noneTrue(boolean[] values) {
        for (boolean value : values) {
            if (value) return false;
        }

        return true;
    }
}