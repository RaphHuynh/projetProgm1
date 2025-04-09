package com.example.projprogrammation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class GpsActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;
    private TextView gpsTitle;
    private ImageView gpsIcon;
    private TextView latitudeData;
    private TextView longitudeData;
    private SwitchCompat gpsSwitch;
    private SensorPreferences sensorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        // Initialisation des vues
        gpsTitle = findViewById(R.id.gps_title);
        gpsIcon = findViewById(R.id.gps_icon);
        latitudeData = findViewById(R.id.latitude_data);
        longitudeData = findViewById(R.id.longitude_data);
        gpsSwitch = findViewById(R.id.gps_switch);

        // Définir le titre et l'icône
        gpsTitle.setText("GPS");
        gpsIcon.setImageResource(R.drawable.ic_gps);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        // Charger l'état sauvegardé du switch
        gpsSwitch.setChecked(sensorPreferences.isGpsEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setGpsEnabled(isChecked);
                updateGpsState(isChecked);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        cancellationTokenSource = new CancellationTokenSource();

        // Initialiser l'état du GPS selon le switch
        updateGpsState(gpsSwitch.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        gpsSwitch.setChecked(sensorPreferences.isGpsEnabled());
        // Mettre à jour l'état du GPS
        updateGpsState(gpsSwitch.isChecked());
    }

    private void updateGpsState(boolean enabled) {
        if (enabled) {
            if (checkLocationPermission()) {
                getLocation();
            } else {
                requestLocationPermission();
            }
        } else {
            // Désactiver les mises à jour de GPS
            latitudeData.setText("--");
            longitudeData.setText("--");
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (gpsSwitch.isChecked()) {
                    getLocation();
                }
            } else {
                // Permission refusée, désactiver le switch
                gpsSwitch.setChecked(false);
                sensorPreferences.setGpsEnabled(false);
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (checkLocationPermission() && gpsSwitch.isChecked()) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            if (latitudeData != null) {
                                latitudeData.setText(String.format("%.6f", latitude));
                            }
                            if (longitudeData != null) {
                                longitudeData.setText(String.format("%.6f", longitude));
                            }
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désactiver les mises à jour de GPS
        latitudeData.setText("--");
        longitudeData.setText("--");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
    }
}
