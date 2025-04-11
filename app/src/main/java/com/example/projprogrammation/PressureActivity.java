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

public class PressureActivity extends AppCompatActivity implements SensorEventListener {
    private TextView pressureData;
    private SwitchCompat pressureSwitch;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private SensorPreferences sensorPreferences;
    private static float pressureValue = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        pressureData = findViewById(R.id.pressure_data);
        pressureSwitch = findViewById(R.id.pressure_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }

        // Charger l'état sauvegardé du switch
        pressureSwitch.setChecked(sensorPreferences.isPressureEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        pressureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setPressureEnabled(isChecked);
                updatePressureState(isChecked);
            }
        });

        // Initialiser l'état du capteur de pression selon le switch
        updatePressureState(pressureSwitch.isChecked());
    }

    private void updatePressureState(boolean enabled) {
        if (enabled) {
            registerListener();
        } else {
            unregisterListener();
            // Réinitialiser la valeur affichée
            pressureData.setText("--");
        }
    }

    private void registerListener() {
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            pressureData.setText("Capteur non disponible");
            pressureSwitch.setEnabled(false);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        pressureSwitch.setChecked(sensorPreferences.isPressureEnabled());
        if (pressureSwitch.isChecked()) {
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
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE && pressureSwitch.isChecked()) {
            pressureValue = event.values[0];
            pressureData.setText(String.format("%.2f hPa", pressureValue));
        }
    }

    public static float getPressureData() {
        return pressureValue;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}
