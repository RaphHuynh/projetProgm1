package com.example.projprogrammation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.CompoundButton;

public class HumidityActivity extends AppCompatActivity implements SensorEventListener {
    private TextView humidityData;
    private SwitchCompat humiditySwitch;
    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private SensorPreferences sensorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        humidityData = findViewById(R.id.humidity_data);
        humiditySwitch = findViewById(R.id.humidity_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        // Charger l'état sauvegardé du switch
        humiditySwitch.setChecked(sensorPreferences.isHumidityEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        humiditySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setHumidityEnabled(isChecked);
                updateHumidityState(isChecked);
            }
        });

        // Initialiser l'état du capteur d'humidité selon le switch
        updateHumidityState(humiditySwitch.isChecked());
    }

    private void updateHumidityState(boolean enabled) {
        if (enabled) {
            registerListener();
        } else {
            unregisterListener();
            // Réinitialiser la valeur affichée
            humidityData.setText("--");
        }
    }

    private void registerListener() {
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            humidityData.setText("Capteur non disponible");
            humiditySwitch.setEnabled(false);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        humiditySwitch.setChecked(sensorPreferences.isHumidityEnabled());
        if (humiditySwitch.isChecked()) {
            registerListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY && humiditySwitch.isChecked()) {
            float humidity = event.values[0];
            humidityData.setText(String.format("%.1f%%", humidity));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}
