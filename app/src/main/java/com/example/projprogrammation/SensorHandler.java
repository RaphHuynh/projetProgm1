package com.example.projprogrammation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorHandler implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor temperatureSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;
    private TextView accelerometerData;
    private TextView temperatureData;
    private TextView pressureData;
    private TextView humidityData;
    private MqttPublishService mqttPublishService;
    private Context context;

    public SensorHandler(Context context, TextView accelerometerData, TextView temperatureData, TextView pressureData, TextView humidityData, MqttPublishService mqttPublishService) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometerData = accelerometerData;
        this.temperatureData = temperatureData;
        this.pressureData = pressureData;
        this.humidityData = humidityData;
        this.mqttPublishService = mqttPublishService;
        this.context = context;

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }
    }

    public void registerListeners() {
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

    public void unregisterListeners() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (accelerometerData != null) {
                accelerometerData.setText(String.format("Accéléromètre:\nX: %.2f\nY: %.2f\nZ: %.2f", x, y, z));
            }

            // Envoyer les données via MqttService
            if (mqttPublishService != null && mqttPublishService.isConnected()) {
                // Publier un message ou effectuer une action
                mqttPublishService.publishMessage("Message depuis SensorHandler");
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            if (temperatureData != null) {
                temperatureData.setText(String.format("Température: %.1f°C", temperature));
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = event.values[0];
            if (pressureData != null) {
                pressureData.setText(String.format("Pression: %.1f hPa", pressure));
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            float humidity = event.values[0];
            if (humidityData != null) {
                humidityData.setText(String.format("Humidité: %.1f%%", humidity));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Can be ignored
    }
}