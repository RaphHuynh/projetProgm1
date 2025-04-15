package com.example.projprogrammation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends BaseActivity {

    private Button startChronoButton;
    private TextView chronoTextView;
    private CardView chronoCardView;
    private Handler handler = new Handler();
    private long startTime = 0;
    private boolean isRunning = false;

    private RecyclerView sensorDataRecyclerView;
    private SensorDataAdapter sensorDataAdapter;
    private List<String> sensorDataList = new ArrayList<>();
    private Handler sensorHandler = new Handler();
    private MqttPublishService mqttPublishService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_record, findViewById(R.id.container));

        // Initialiser la RecyclerView
        sensorDataRecyclerView = findViewById(R.id.sensorDataRecyclerView);
        sensorDataAdapter = new SensorDataAdapter(sensorDataList);
        sensorDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sensorDataRecyclerView.setAdapter(sensorDataAdapter);

        // Démarrer la collecte des données des capteurs
        startSensorDataCollection();

        startChronoButton = findViewById(R.id.startChronoButton);
        chronoTextView = findViewById(R.id.chronoTextView);
        chronoCardView = findViewById(R.id.chronoCardView);

        // Récupérer l'état du chronomètre
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("startTime", 0);
        isRunning = prefs.getBoolean("isRunning", false);

        if (isRunning) {
            updateChrono();
        }

        // Lier le service MQTT
        Intent mqttServiceIntent = new Intent(this, MqttPublishService.class);
        bindService(mqttServiceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MqttPublishService.LocalBinder binder = (MqttPublishService.LocalBinder) service;
                mqttPublishService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mqttPublishService = null;
            }
        }, Context.BIND_AUTO_CREATE);

        startChronoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    isRunning = true;
                    startChronoButton.setText("Stop");
                    updateChrono();
                    if (mqttPublishService != null) {
                        mqttPublishService.setChronoRunning(true); // Activer l'envoi MQTT
                    }
                } else {
                    isRunning = false;
                    handler.removeCallbacksAndMessages(null);
                    startChronoButton.setText("Lancer l'enregistrement");
                    if (mqttPublishService != null) {
                        mqttPublishService.setChronoRunning(false); // Désactiver l'envoi MQTT
                    }
                }
            }
        });
    }

    private void updateChrono() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedMillis = System.currentTimeMillis() - startTime;
                    int seconds = (int) (elapsedMillis / 1000) % 60;
                    int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
                    int hours = (int) (elapsedMillis / (1000 * 60 * 60));
                    String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    chronoTextView.setText(time);
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void startSensorDataCollection() {
        sensorHandler.post(new Runnable() {
            @Override
            public void run() {
                // Simuler la collecte des données des capteurs activés
                String simulatedData = "Donnée capteur : " + System.currentTimeMillis();
                sensorDataList.add(simulatedData);

                // Mettre à jour la RecyclerView
                sensorDataAdapter.notifyItemInserted(sensorDataList.size() - 1);

                // Replanifier la collecte toutes les 2 secondes
                sensorHandler.postDelayed(this, 2000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Sauvegarder l'état du chronomètre
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTime", startTime);
        editor.putBoolean("isRunning", isRunning);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttPublishService != null) {
            unbindService(new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {}

                @Override
                public void onServiceDisconnected(ComponentName name) {}
            });
        }
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_record;
    }
}
