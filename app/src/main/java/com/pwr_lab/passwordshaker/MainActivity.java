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
        passGenerator.setDefaultOptions();

        // save default settings values
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // shared prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefDigits = prefs.getBoolean(SettingsActivity.KEY_SWITCH_DIGITS, false);
        Toast.makeText(this, "" + prefDigits, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}