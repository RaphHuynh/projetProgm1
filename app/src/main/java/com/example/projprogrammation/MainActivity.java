package com.example.projprogrammation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;
    private TextView accelerometerData;
    private TextView gpsData;
    private Location lastLocation;
    private double distanceParcourue = 0;
    private Sensor temperatureSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;
    private TextView temperatureData;
    private TextView pressureData;
    private TextView humidityData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometerData = findViewById(R.id.accelerometer_data);
        gpsData = findViewById(R.id.gps_data);
        temperatureData = findViewById(R.id.temperature_data);
        pressureData = findViewById(R.id.pressure_data);
        humidityData = findViewById(R.id.humidity_data);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);  // Initialisation correcte

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        requestLocationPermission();
        startLocationUpdates();
    }


    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Enable GPS");
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            float speed = location.getSpeed(); // Vitesse en m/s

                            // Calcul de la distance parcourue
                            if (lastLocation != null) {
                                float distance = lastLocation.distanceTo(location); // En mètres
                                distanceParcourue += distance;
                            }
                            lastLocation = location;

                            gpsData.setText(String.format("GPS:\nLat: %.5f\nLon: %.5f\nVitesse GPS: %.2f m/s\nDistance: %.2f m",
                                    latitude, longitude, speed, distanceParcourue));
                        }
                    }
                });
    }

    private void startLocationUpdates() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                getLocation();
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(locationRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (temperatureSensor != null) {
            sensorManager.unregisterListener(this, temperatureSensor);
        }
        if (pressureSensor != null) {
            sensorManager.unregisterListener(this, pressureSensor);
        }
        if (humiditySensor != null) {
            sensorManager.unregisterListener(this, humiditySensor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(locationRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Affichage des valeurs
            accelerometerData.setText(String.format("Accéléromètre:\nX: %.2f\nY: %.2f\nZ: %.2f", x, y, z));
        }
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            temperatureData.setText(String.format("Température: %.1f°C", temperature));
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = event.values[0];
            pressureData.setText(String.format("Pression: %.1f hPa", pressure));
        }

        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            float humidity = event.values[0];
            humidityData.setText(String.format("Humidité: %.1f%%", humidity));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Peut être ignoré
    }
}
