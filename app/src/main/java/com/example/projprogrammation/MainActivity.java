package com.example.projprogrammation;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MqttPublishService MqttPublishService;
    private SensorHandler sensorHandler;
    private LocationHandler locationHandler;
    private NetworkHandler networkHandler;
    private TextView accelerometerData;
    private TextView gpsData;
    private TextView temperatureData;
    private TextView pressureData;
    private TextView humidityData;
    private TextView networkData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        accelerometerData = findViewById(R.id.accelerometer_data);
        gpsData = findViewById(R.id.gps_data);
        temperatureData = findViewById(R.id.temperature_data);
        pressureData = findViewById(R.id.pressure_data);
        humidityData = findViewById(R.id.humidity_data);
        networkData = findViewById(R.id.network_data);
        MqttPublishService = new MqttPublishService();

        // Initialize Handlers
        sensorHandler = new SensorHandler(this, accelerometerData, temperatureData, pressureData, humidityData, MqttPublishService);
        locationHandler = new LocationHandler(this, gpsData);
        networkHandler = new NetworkHandler(networkData);

        // Start network updates
        networkHandler.startNetworkUpdates();

        // Request location permission and start updates
        locationHandler.requestLocationPermission(this);
        locationHandler.startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHandler.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHandler.unregisterListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHandler.stopLocationUpdates();
    }
}