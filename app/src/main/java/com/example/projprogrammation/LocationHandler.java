package com.example.projprogrammation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandler {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;
    private TextView gpsData;
    private Location lastLocation;
    private double distanceParcourue = 0;
    private Context context;

    public LocationHandler(Context context, TextView gpsData) {
        this.context = context;
        this.gpsData = gpsData;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void requestLocationPermission(MainActivity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LocationHandler", "Enable GPS");
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener((MainActivity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            float speed = location.getSpeed(); // Speed in m/s

                            // Calculate the distance traveled
                            if (lastLocation != null) {
                                float distance = lastLocation.distanceTo(location); // In meters
                                distanceParcourue += distance;
                            }
                            lastLocation = location;

                            gpsData.setText(String.format("GPS:\nLat: %.5f\nLon: %.5f\nVitesse GPS: %.2f m/s\nDistance: %.2f m",
                                    latitude, longitude, speed, distanceParcourue));
                        }
                    }
                });
    }

    public void startLocationUpdates() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                getLocation();
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(locationRunnable);
    }

    public void stopLocationUpdates() {
        handler.removeCallbacks(locationRunnable);
    }
}