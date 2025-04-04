package com.example.projprogrammation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublishService extends Service {

    private static final String TAG = "MqttPublishService";
    private static final String SERVER_URI = "tcp://194.57.103.203:1883";
    private static final String TOPIC = "vehicle";
    MqttAndroidClient mqttAndroidClient;
    private final IBinder binder = new LocalBinder();
    private boolean isConnected = false;
    private Handler handler = new Handler();
    private Runnable publishRunnable;
    private static final int PUBLISH_INTERVAL = 5000; // Intervalle en millisecondes (5 secondes)

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
    }

    private void initializeMqttClient() {
        if (mqttAndroidClient == null) {
            String clientId = MqttClient.generateClientId();
            mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), SERVER_URI, clientId);

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "Connection lost: " + cause.getMessage());
                    isConnected = false;
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "Message arrived: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery complete");
                }
            });

            connect();
        }

    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connection success");
                    isConnected = true;
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Connection failure: " + exception.getMessage());
                    isConnected = false;
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Error connecting to MQTT server", e);
            isConnected = false;
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(TOPIC, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed to topic: " + TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe to topic: " + TOPIC + ", error: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Error subscribing to topic", e);
        }
    }

    public void publishMessage(String message) {
        if (mqttAndroidClient == null) {
            initializeMqttClient();
        }
        if (mqttAndroidClient != null && isConnected) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                mqttAndroidClient.publish(TOPIC, mqttMessage);
                Log.d(TAG, "Message published: " + message);
            } catch (MqttException e) {
                Log.e(TAG, "Error publishing message", e);
            }
        } else {
            Log.d(TAG, "Not connected to MQTT server or client not initialized. Cannot publish message.");
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
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                String message = generateMessage(); // Générer un message à publier
                publishMessage(message); // Publier le message
                handler.postDelayed(this, PUBLISH_INTERVAL); // Replanifier la tâche
            }
        };
        handler.post(publishRunnable); // Démarrer la tâche
    }

    private String generateMessage() {
        // Générer un message à envoyer (exemple : données simulées ou réelles)
        return "Données envoyées à " + System.currentTimeMillis();
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
    }

    private void disconnect() {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Disconnected from MQTT server");
                        isConnected = false;
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "Failed to disconnect from MQTT server: " + exception.getMessage());
                    }
                });
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting from MQTT server", e);
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
}