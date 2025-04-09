package com.example.projprogrammation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe utilitaire pour gérer les préférences des capteurs
 */
public class SensorPreferences {
    private static final String PREF_NAME = "sensor_preferences";
    private static final String ACCELEROMETER_ENABLED = "accelerometer_enabled";
    private static final String GYROSCOPE_ENABLED = "gyroscope_enabled";
    private static final String MAGNETOMETER_ENABLED = "magnetometer_enabled";
    private static final String TEMPERATURE_ENABLED = "temperature_enabled";
    private static final String PRESSURE_ENABLED = "pressure_enabled";
    private static final String HUMIDITY_ENABLED = "humidity_enabled";
    private static final String GPS_ENABLED = "gps_enabled";

    private SharedPreferences preferences;

    public SensorPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Méthodes pour accéléromètre
    public boolean isAccelerometerEnabled() {
        return preferences.getBoolean(ACCELEROMETER_ENABLED, true);
    }

    public void setAccelerometerEnabled(boolean enabled) {
        preferences.edit().putBoolean(ACCELEROMETER_ENABLED, enabled).apply();
    }

    // Méthodes pour gyroscope
    public boolean isGyroscopeEnabled() {
        return preferences.getBoolean(GYROSCOPE_ENABLED, true);
    }

    public void setGyroscopeEnabled(boolean enabled) {
        preferences.edit().putBoolean(GYROSCOPE_ENABLED, enabled).apply();
    }

    // Méthodes pour magnétomètre
    public boolean isMagnetometerEnabled() {
        return preferences.getBoolean(MAGNETOMETER_ENABLED, true);
    }

    public void setMagnetometerEnabled(boolean enabled) {
        preferences.edit().putBoolean(MAGNETOMETER_ENABLED, enabled).apply();
    }

    // Méthodes pour température
    public boolean isTemperatureEnabled() {
        return preferences.getBoolean(TEMPERATURE_ENABLED, true);
    }

    public void setTemperatureEnabled(boolean enabled) {
        preferences.edit().putBoolean(TEMPERATURE_ENABLED, enabled).apply();
    }

    // Méthodes pour pression
    public boolean isPressureEnabled() {
        return preferences.getBoolean(PRESSURE_ENABLED, true);
    }

    public void setPressureEnabled(boolean enabled) {
        preferences.edit().putBoolean(PRESSURE_ENABLED, enabled).apply();
    }

    // Méthodes pour humidité
    public boolean isHumidityEnabled() {
        return preferences.getBoolean(HUMIDITY_ENABLED, true);
    }

    public void setHumidityEnabled(boolean enabled) {
        preferences.edit().putBoolean(HUMIDITY_ENABLED, enabled).apply();
    }

    // Méthodes pour GPS
    public boolean isGpsEnabled() {
        return preferences.getBoolean(GPS_ENABLED, true);
    }

    public void setGpsEnabled(boolean enabled) {
        preferences.edit().putBoolean(GPS_ENABLED, enabled).apply();
    }
}
