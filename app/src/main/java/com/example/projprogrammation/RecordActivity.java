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

public class RecordActivity extends BaseActivity {

    private Button startChronoButton;
    private TextView chronoTextView;
    private CardView chronoCardView;
    private Handler handler = new Handler();
    private long startTime = 0;
    private boolean isRunning = false;

    private MqttPublishService mqttPublishService;
    private boolean isServiceBound = false; // Variable pour suivre l'état du service
    private ServiceConnection mqttServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_record, findViewById(R.id.container));

        startChronoButton = findViewById(R.id.startChronoButton);
        chronoTextView = findViewById(R.id.chronoTextView);
        chronoCardView = findViewById(R.id.chronoCardView);

        // Récupérer l'état du chronomètre
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("startTime", 0);
        isRunning = prefs.getBoolean("isRunning", false);

        // Afficher le bon texte sur le bouton selon l'état
        if (isRunning) {
            startChronoButton.setText("Stop");
            updateChrono();
        } else {
            startChronoButton.setText("Lancer l'enregistrement");
        }

        // Initialiser la connexion au service
        mqttServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MqttPublishService.LocalBinder binder = (MqttPublishService.LocalBinder) service;
                mqttPublishService = binder.getService();
                isServiceBound = true; // Marquer le service comme enregistré
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mqttPublishService = null;
                isServiceBound = false; // Marquer le service comme non enregistré
            }
        };

        // Lier le service
        Intent mqttServiceIntent = new Intent(this, MqttPublishService.class);
        bindService(mqttServiceIntent, mqttServiceConnection, Context.BIND_AUTO_CREATE);

        startChronoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    isRunning = true;
                    startChronoButton.setText("Stop");
                    updateChrono();
                    if (mqttPublishService != null) {
                        mqttPublishService.setChronoRunning(true);
                    }
                } else {
                    isRunning = false;
                    handler.removeCallbacksAndMessages(null);
                    startChronoButton.setText("Lancer l'enregistrement");
                    if (mqttPublishService != null) {
                        mqttPublishService.setChronoRunning(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Synchroniser l'état du bouton à chaque retour sur la page
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        isRunning = prefs.getBoolean("isRunning", false);
        if (isRunning) {
            startChronoButton.setText("Stop");
            updateChrono();
        } else {
            startChronoButton.setText("Lancer l'enregistrement");
        }
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
        // Vérifier si le service est enregistré avant de le désenregistrer
        if (isServiceBound) {
            unbindService(mqttServiceConnection);
            isServiceBound = false;
        }
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_record;
    }
}
