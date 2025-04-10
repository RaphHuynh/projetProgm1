package com.example.projprogrammation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "location_service_channel";
    private static final int NOTIFICATION_ID = 12345;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private double distance = 0.0;

    // Binder pour communiquer avec les activités
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        createLocationRequest();
        createLocationCallback();
        
        // Démarrer en tant que service foreground pour éviter d'être tué par le système
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Démarrer la surveillance de localisation
        startLocationUpdates();
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
                        // Calculer la distance si nous avons une position précédente
                        if (lastLocation != null) {
                            distance += lastLocation.distanceTo(location);
                        }
                        
                        lastLocation = location;
                        Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                        
                        // Diffuser un broadcast local pour informer les activités
                        Intent intent = new Intent("location-update");
                        intent.putExtra("latitude", location.getLatitude());
                        intent.putExtra("longitude", location.getLongitude());
                        intent.putExtra("speed", location.getSpeed());
                        intent.putExtra("distance", distance);
                        sendBroadcast(intent);
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission: " + e.getMessage());
        }
    }

    private Notification createNotification() {
        createNotificationChannel();
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gps)
                .setContentTitle("GPS actif")
                .setContentText("Suivi de localisation en cours")
                .setPriority(NotificationCompat.PRIORITY_LOW);
                
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de service de localisation",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Canal utilisé pour le service de localisation en arrière-plan");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Si le service est tué, le redémarrer
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Méthodes accessibles aux activités
    public Location getLastLocation() {
        return lastLocation;
    }
    
    public double getDistance() {
        return distance;
    }
}
