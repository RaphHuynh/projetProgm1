package com.example.projprogrammation;

import android.content.Context; // Ajout de l'import manquant
import org.json.JSONObject;

public class SensorDataProvider {
    private static SensorDataProvider instance;
    private SensorPreferences sensorPreferences;

    private SensorDataProvider(Context context) {
        sensorPreferences = SensorPreferences.getInstance(context); // Passer le contexte
    }

    public static SensorDataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new SensorDataProvider(context);
        }
        return instance;
    }

    public JSONObject getAccelerometerData() {
        if (sensorPreferences.isAccelerometerEnabled()) {
            // Récupérer les données de l'accéléromètre
            return createSensorData("x", AccelerometerActivity.getXData(),
                                    "y", AccelerometerActivity.getYData(),
                                    "z", AccelerometerActivity.getZData());
        }
        return null;
    }

    public JSONObject getGyroscopeData() {
        if (sensorPreferences.isGyroscopeEnabled()) {
            // Récupérer les données du gyroscope
            return createSensorData("x", GyroscopeActivity.getXData(),
                                    "y", GyroscopeActivity.getYData(),
                                    "z", GyroscopeActivity.getZData());
        }
        return null;
    }

    public JSONObject getMagnetometerData() {
        if (sensorPreferences.isMagnetometerEnabled()) {
            // Récupérer les données du magnétomètre
            return createSensorData("x", MagnetometerActivity.getXData(),
                                    "y", MagnetometerActivity.getYData(),
                                    "z", MagnetometerActivity.getZData());
        }
        return null;
    }

    public JSONObject getTemperatureData() {
        if (sensorPreferences.isTemperatureEnabled()) {
            // Récupérer les données de température
            return createSensorData("temperature", TemperatureActivity.getTemperatureData());
        }
        return null;
    }

    public JSONObject getPressureData() {
        if (sensorPreferences.isPressureEnabled()) {
            // Récupérer les données de pression
            return createSensorData("pressure", PressureActivity.getPressureData());
        }
        return null;
    }

    public JSONObject getGpsData() {
        if (sensorPreferences.isGpsEnabled()) {
            // Récupérer les données GPS
            return createSensorData("latitude", GpsActivity.getLatitudeData(),
                                    "longitude", GpsActivity.getLongitudeData());
        }
        return null;
    }

    private JSONObject createSensorData(Object... keyValues) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < keyValues.length; i += 2) {
                jsonObject.put(keyValues[i].toString(), keyValues[i + 1]);
            }
            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAccelerometerEnabled() {
        return sensorPreferences.isAccelerometerEnabled();
    }

    public boolean isGyroscopeEnabled() {
        return sensorPreferences.isGyroscopeEnabled();
    }

    public boolean isMagnetometerEnabled() {
        return sensorPreferences.isMagnetometerEnabled();
    }

    public boolean isTemperatureEnabled() {
        return sensorPreferences.isTemperatureEnabled();
    }

    public boolean isPressureEnabled() {
        return sensorPreferences.isPressureEnabled();
    }

    public boolean isGpsEnabled() {
        return sensorPreferences.isGpsEnabled();
    }
}
