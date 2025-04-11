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

public class TemperatureActivity extends AppCompatActivity implements SensorEventListener {
    private TextView temperatureData;
    private SwitchCompat temperatureSwitch;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private SensorPreferences sensorPreferences;
    private static float temperatureValue = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        temperatureData = findViewById(R.id.temperature_data);
        temperatureSwitch = findViewById(R.id.temperature_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            
            // Essayer d'autres types de capteurs de température si le principal n'est pas disponible
            if (temperatureSensor == null) {
                temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
            }
        }

        // Charger l'état sauvegardé du switch
        temperatureSwitch.setChecked(sensorPreferences.isTemperatureEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setTemperatureEnabled(isChecked);
                updateTemperatureState(isChecked);
            }
        });

        // Initialiser l'état du capteur de température selon le switch
        updateTemperatureState(temperatureSwitch.isChecked());
    }

    private void updateTemperatureState(boolean enabled) {
        if (enabled) {
            registerListener();
        } else {
            unregisterListener();
            // Réinitialiser la valeur affichée
            temperatureData.setText("--");
        }
    }

    private void registerListener() {
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            temperatureData.setText("Capteur non disponible");
            temperatureSwitch.setEnabled(false);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        temperatureSwitch.setChecked(sensorPreferences.isTemperatureEnabled());
        if (temperatureSwitch.isChecked()) {
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
        if ((event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE || 
             event.sensor.getType() == Sensor.TYPE_TEMPERATURE) && 
            temperatureSwitch.isChecked()) {
            temperatureValue = event.values[0];
            temperatureData.setText(String.format("%.1f °C", temperatureValue));
        }
    }

    public static float getTemperatureData() {
        return temperatureValue;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}
