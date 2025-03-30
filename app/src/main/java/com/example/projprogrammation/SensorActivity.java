package com.example.projprogrammation;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class SensorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chargez le contenu de activity_sensor dans le conteneur de BaseActivity
        getLayoutInflater().inflate(R.layout.activity_sensor, findViewById(R.id.container));
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_sensor;
    }

    public void openAccelerometerActivity(View view) {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }

    public void openTemperatureActivity(View view) {
        Intent intent = new Intent(this, TemperatureActivity.class);
        startActivity(intent);
    }

    public void openPressureActivity(View view) {
        Intent intent = new Intent(this, PressureActivity.class);
        startActivity(intent);
    }

    public void openHumidityActivity(View view) {
        Intent intent = new Intent(this, HumidityActivity.class);
        startActivity(intent);
    }

    public void openMagnetometerActivity(View view) {
        Intent intent = new Intent(this, MagnetometerActivity.class);
        startActivity(intent);
    }

    public void openGyroscopeActivity(View view) {
        Intent intent = new Intent(this, GyroscopeActivity.class);
        startActivity(intent);
    }

    public void openGpsActivity(View view) {
        Intent intent = new Intent(this, GpsActivity.class);
        startActivity(intent);
    }
}
