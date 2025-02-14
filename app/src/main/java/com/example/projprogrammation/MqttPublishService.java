package com.example.projprogrammation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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

public class MqttService extends Service {

    private static final String TAG = "MqttService";
    private static final String SERVER_URI = "tcp://194.57.103.203:1883";
    private static final String TOPIC = "vehicule";
    private MqttAndroidClient mqttAndroidClient;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MqttService getService() {
            return MqttService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), SERVER_URI, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost: " + cause.getMessage());
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

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connection success");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Connection failure: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Error connecting to MQTT server", e);
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
        try {
            if (mqttAndroidClient.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                mqttAndroidClient.publish(TOPIC, mqttMessage);
                Log.d(TAG, "Message published: " + message);
            } else {
                Log.d(TAG, "Not connected to MQTT server. Cannot publish message.");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error publishing message", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void disconnect() {
        try {
            if (mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Disconnected from MQTT server");
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
        return binder;
    }

}