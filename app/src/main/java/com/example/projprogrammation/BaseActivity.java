package com.example.projprogrammation;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    private MqttPublishService mqttPublishService;
    private boolean isServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(getActiveMenuItemId());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == getActiveMenuItemId()) {
                return true;
            }

            if (itemId == R.id.navigation_home) {
                navigateToActivity(HomeActivity.class);
            } else if (itemId == R.id.navigation_record) {
                navigateToActivity(RecordActivity.class);
            } else if (itemId == R.id.navigation_history) {
                navigateToActivity(HistoryActivity.class);
            } else if (itemId == R.id.navigation_sensor) {
                navigateToActivity(SensorActivity.class);
            }
            return true;
        });

        // Lancer et lier le service MQTT
        Intent mqttServiceIntent = new Intent(this, MqttPublishService.class);
        startService(mqttServiceIntent);
        bindService(mqttServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MqttPublishService.LocalBinder binder = (MqttPublishService.LocalBinder) service;
            mqttPublishService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mqttPublishService = null;
            isServiceBound = false;
        }
    };

    protected abstract int getActiveMenuItemId();

    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Désactiver le bouton retour
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Détacher le service MQTT
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
}
