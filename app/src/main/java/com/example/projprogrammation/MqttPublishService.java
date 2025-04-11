package com.example.projprogrammation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
import org.json.JSONObject;

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

    private void startPublishing() {
        if (isPublishing) {
            Log.d(TAG, "Publishing already in progress, skipping duplicate start.");
            return; // Éviter de démarrer une nouvelle tâche si elle est déjà en cours
        }

        isPublishing = true; // Marquer comme en cours de publication
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                String message = generateSensorDataMessage(); // Générer un message JSON avec les données des capteurs
                if (message != null) { // Vérifier si un message doit être envoyé
                    publishMessage(message); // Publier le message
                }
                handler.postDelayed(this, PUBLISH_INTERVAL); // Replanifier la tâche toutes les secondes
            }
        };
        handler.post(publishRunnable); // Démarrer la tâche
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
}