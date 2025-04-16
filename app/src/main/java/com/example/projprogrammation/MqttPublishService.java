package com.example.projprogrammation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class MqttPublishService extends Service {

    private static final String TAG = "MqttPublishService";
    private static final String SERVER_URI = "tcp://194.57.103.203:1883";
    private static final String TOPIC = "vehicle";
    private MqttClient mqttClient;
    private final IBinder binder = new LocalBinder();
    private Handler handler = new Handler();
    private Runnable publishRunnable;
    private static final int PUBLISH_INTERVAL = 1000; // Intervalle en millisecondes (1 seconde)
    private SensorDataProvider sensorDataProvider; // Interface pour récupérer les données des capteurs
    private boolean isPublishing = false; // Drapeau pour éviter l'envoi en double
    private boolean isChronoRunning = false;
    private File currentRecordFile;
    private File currentMetaFile;
    private long recordStartTime = 0;

    public class LocalBinder extends Binder {
        public MqttPublishService getService() {
            return MqttPublishService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");
        initializeMqttClient();
        sensorDataProvider = SensorDataProvider.getInstance(this); // Passer le contexte de service
    }

    private void initializeMqttClient() {
        try {
            mqttClient = new MqttClient(SERVER_URI, MqttClient.generateClientId(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            mqttClient.connect(options);
            Log.d(TAG, "Connected to MQTT broker");
        } catch (MqttException e) {
            Log.e(TAG, "Error initializing MQTT client", e);
        }
    }

    public void publishMessage(String message) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttClient.publish(TOPIC, mqttMessage);
                Log.d(TAG, "Message published: " + message);
            } catch (MqttException e) {
                Log.e(TAG, "Error publishing message", e);
            }
        } else {
            Log.d(TAG, "MQTT client not connected. Cannot publish message.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started");

        // Démarrer la publication périodique
        startPublishing();

        return START_STICKY;
    }

    public void setChronoRunning(boolean isRunning) {
        // Si on démarre un nouveau record, on crée un nouveau fichier
        if (isRunning && !isChronoRunning) {
            createNewRecordFile();
        }
        // Si on arrête le record, on met à jour le fichier meta
        if (!isRunning && isChronoRunning) {
            updateMetaFileOnStop();
        }
        isChronoRunning = isRunning;
    }

    private void startPublishing() {
        if (isPublishing) return;
        isPublishing = true;
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                if (isChronoRunning) {
                    String message = generateSensorDataMessage();
                    if (message != null) {
                        publishMessage(message);
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        try {
                            JSONObject sensorData = new JSONObject(message);
                            for (Iterator<String> it = sensorData.keys(); it.hasNext(); ) { // Remplacer keySet() par keys()
                                String key = it.next();
                                writeDataToFile(timestamp, key, sensorData.getJSONObject(key));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing sensor data message", e);
                        }
                    }
                }
                handler.postDelayed(this, PUBLISH_INTERVAL);
            }
        };
        handler.post(publishRunnable);
    }

    private String generateSensorDataMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            boolean hasData = false;

            if (sensorDataProvider != null) {
                // Récupérer les données des capteurs activés
                if (sensorDataProvider.isAccelerometerEnabled()) {
                    jsonObject.put("accelerometer", sensorDataProvider.getAccelerometerData());
                    hasData = true;
                }
                if (sensorDataProvider.isGyroscopeEnabled()) {
                    jsonObject.put("gyroscope", sensorDataProvider.getGyroscopeData());
                    hasData = true;
                }
                if (sensorDataProvider.isMagnetometerEnabled()) {
                    jsonObject.put("magnetometer", sensorDataProvider.getMagnetometerData());
                    hasData = true;
                }
                if (sensorDataProvider.isTemperatureEnabled()) {
                    jsonObject.put("temperature", sensorDataProvider.getTemperatureData());
                    hasData = true;
                }
                if (sensorDataProvider.isPressureEnabled()) {
                    jsonObject.put("pressure", sensorDataProvider.getPressureData());
                    hasData = true;
                }
                if (sensorDataProvider.isGpsEnabled()) {
                    jsonObject.put("gps", sensorDataProvider.getGpsData());
                    hasData = true;
                }
            }

            // Si aucun capteur n'est activé, retourner null pour indiquer qu'aucun message ne doit être envoyé
            if (!hasData) {
                return null;
            }

            return jsonObject.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error generating sensor data message", e);
            return null; // Retourner null en cas d'erreur
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPublishing(); // Arrêter la publication périodique
        disconnect();
        Log.d(TAG, "onDestroy: Service destroyed");
    }

    private void stopPublishing() {
        if (handler != null && publishRunnable != null) {
            handler.removeCallbacks(publishRunnable); // Arrêter la tâche
        }
        isPublishing = false; // Réinitialiser le drapeau
    }

    private void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                Log.d(TAG, "Disconnected from MQTT broker");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting from MQTT broker", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Service bound");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Service unbound");
        return super.onUnbind(intent);
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private void createNewRecordFile() {
        recordStartTime = System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(recordStartTime));
        String metaName = "record_" + timestamp + ".json";
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Records");
        if (!directory.exists()) directory.mkdirs();
        currentMetaFile = new File(directory, metaName);
        try {
            // Créer le fichier de métadonnées JSON
            JSONObject meta = new JSONObject();
            meta.put("start", recordStartTime);
            meta.put("duration", 0); // sera mis à jour à l'arrêt
            meta.put("end", 0);
            meta.put("accelerometer", new JSONArray());
            meta.put("gyroscope", new JSONArray());
            meta.put("magnetometer", new JSONArray());
            meta.put("temperature", new JSONArray());
            meta.put("pressure", new JSONArray());
            meta.put("gps", new JSONArray());
            try (FileOutputStream fos = new FileOutputStream(currentMetaFile)) {
                fos.write(meta.toString().getBytes());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating meta file", e);
        }
    }

    private void updateMetaFileOnStop() {
        if (currentMetaFile != null && currentMetaFile.exists() && recordStartTime != 0) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(currentMetaFile.toPath()));
                JSONObject meta = new JSONObject(content);
                long end = System.currentTimeMillis();
                meta.put("end", end);
                meta.put("duration", end - recordStartTime);
                try (FileOutputStream fos = new FileOutputStream(currentMetaFile)) {
                    fos.write(meta.toString().getBytes());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating meta file", e);
            }
        }
    }

    private void writeDataToFile(String timestamp, String sensor, JSONObject sensorData) {
        if (currentMetaFile != null) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(currentMetaFile.toPath()));
                JSONObject meta = new JSONObject(content);
                JSONArray sensorArray = meta.optJSONArray(sensor);
                if (sensorArray != null) {
                    sensorData.put("timestamp", timestamp);
                    sensorArray.put(sensorData);
                    meta.put(sensor, sensorArray);
                }
                try (FileOutputStream fos = new FileOutputStream(currentMetaFile)) {
                    fos.write(meta.toString().getBytes());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error writing sensor data to file", e);
            }
        }
    }
}