package com.example.projprogrammation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class GpsActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView gpsTitle;
    private ImageView gpsIcon;
    private TextView latitudeData;
    private TextView longitudeData;
    private SwitchCompat gpsSwitch;
    private SensorPreferences sensorPreferences;
    
    // Variables pour les mises à jour continues
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private Location lastLocation = null;
    private double distanceParcourue = 0.0;

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

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Créer les objets de requête et de callback de localisation
        createLocationRequest();
        createLocationCallback();
        
        // Ajouter un écouteur de changement d'état pour le switch
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setGpsEnabled(isChecked);
                updateGpsState(isChecked);
            }
        });
        
        // Initialiser l'état du GPS selon le switch
        updateGpsState(gpsSwitch.isChecked());
    }
    
    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000) // 1 seconde
                .setMinUpdateIntervalMillis(500) // 0.5 seconde minimum
                .build();
    }
    
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        float speed = location.getSpeed(); // Vitesse en m/s
                        
                        // Calculer la distance parcourue
                        if (lastLocation != null) {
                            float distance = lastLocation.distanceTo(location); // En mètres
                            distanceParcourue += distance;
                        }
                        lastLocation = location;
                        
                        // Mise à jour de l'UI
                        latitudeData.setText(String.format("%.6f", latitude));
                        longitudeData.setText(String.format("%.6f", longitude));
                    }
                }
            }
        };
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
                startLocationUpdates();
            } else {
                requestLocationPermission();
            }
        } else {
            // Désactiver les mises à jour de GPS
            stopLocationUpdates();
            latitudeData.setText("--");
            longitudeData.setText("--");
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        if (!requestingLocationUpdates) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
            requestingLocationUpdates = true;
        }
    }
    
    private void stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdates = false;
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
                    startLocationUpdates();
                }
            } else {
                // Permission refusée, désactiver le switch
                gpsSwitch.setChecked(false);
                sensorPreferences.setGpsEnabled(false);
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Si GPS activé mais activité en arrière-plan, continuer les mises à jour
        // mais ne pas afficher les données
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrêter les mises à jour quand l'activité est détruite
        if (gpsSwitch.isChecked()) {
            // Ici, nous n'arrêtons pas les mises à jour si le GPS est activé
            // mais nous pourrions créer un service en arrière-plan pour continuer les mises à jour
        } else {
            stopLocationUpdates();
        }
    }
}
